package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.webcollector.crawler.BasicCrawler;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by wlcheng on 12/28/15.
 */
public class ControllerTemplate {

    protected String nextPageCrawlerClassName = "";
    protected String eduCrawlerClassName ="";
    protected String parseCommentsClassName = "";
    protected boolean isComments = false;

    protected boolean isSelenium = false;

    public void setNextPageCrawlerClassName(String nextPageCrawlerClassName) {
        this.nextPageCrawlerClassName = nextPageCrawlerClassName;
    }

    public void setEduCrawlerClassName(String eduCrawlerClassName) {
        this.eduCrawlerClassName = eduCrawlerClassName;
    }

    public void setParseCommentsClassName(String parseCommentsClassName) {
        this.parseCommentsClassName = parseCommentsClassName;
    }

    public static final Logger LOG = LoggerFactory.getLogger(ControllerTemplate.class);
    Configuration conf = null;
    public ControllerTemplate(Configuration conf){
        this.conf = conf;
    }

    public ControllerTemplate(Configuration conf,boolean isComments){
        this.conf = conf;
        this.isComments = isComments;
    }

    public ControllerTemplate(Configuration conf,boolean isComments,boolean isSelenium){
        this.conf = conf;
        this.isComments = isComments;
        this.isSelenium = isSelenium;
    }
    public void run() {
        String suffix = conf.get("crawler.suffix");
        String crawlDb = conf.get(suffix+".crawldb");
        String crawlNextDb = crawlDb+".next";

        BasicCrawler baseEduNextPageCrawler = null;
        try{
            if(isSelenium){
                System.out.println(nextPageCrawlerClassName);
                baseEduNextPageCrawler = (BaseEduNextPageRamCrawler) Class.forName(nextPageCrawlerClassName).newInstance();
            }else{
                Class cls = Class.forName(nextPageCrawlerClassName);
                Class[] paramTypes = { String.class,boolean.class};
                Object[] params = {crawlNextDb, true};                // 方法传入的参数
                Constructor con = cls.getConstructor(paramTypes);     //主要就是这句了
                baseEduNextPageCrawler = (BaseEduNextPageBreadthCrawler) con.newInstance(params);
//                else
//                    baseEduNextPageCrawler = (BaseEduNextPageRamCrawler) con.newInstance(params);
            }
        }catch (ClassNotFoundException e){
            LOG.info("Initial the baseEduNextPageBreadthCrawler class failed!\r\n" +
                    "Please make sure that the class name contains the absolute package path.\r\n"+
                    "The current class name is :"+nextPageCrawlerClassName);
            return ;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        baseEduNextPageCrawler.setConf(conf);
        baseEduNextPageCrawler.setResumable(true);


        CrawlDatums crawlDatums = new CrawlDatums();


        String seedPath = conf.get(suffix + ".path.seeds*");;
        List<String> urls = null;                                       //第二步:加载配置的seed
        try {
            urls = FileUtils.readFileAsList(edu.ecnu.util.Config.crawlerRoot+ File.separator+seedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crawlDatums.add(urls);
        baseEduNextPageCrawler.addSeed(crawlDatums,true);

        List<String> regs = conf.getValues(suffix+".next.reg.*");      //第三步:加载配置的正则表达式
        baseEduNextPageCrawler.addRegexs(regs);

        try {
//            baseEduNextPageCrawler.start(Config.MAX_DEPTH);
            baseEduNextPageCrawler.start(4);
        } catch (Exception e) {
            e.printStackTrace();
        }

        baseEduNextPageCrawler.stop();
        LOG.info("Finish crawl all the next pages. The total next pages' count is :" +
                baseEduNextPageCrawler.getSeeds().size());


        /**********************************************/



        BaseEduCrawler baseEduCrawler = null;
        Class cls = null;
        try {
            cls = Class.forName(eduCrawlerClassName);
        } catch (ClassNotFoundException e) {
            LOG.info("Initial the BaseEduCrawler class failed!\r\n" +
                    "Please make sure that the class name contains the absolute package path.\r\n"+
                    "The current class name is :"+eduCrawlerClassName);
            return ;
        }
        Class[] paramTypes = { String.class,boolean.class};
        Object[] params = {crawlDb, true}; // 方法传入的参数
        Constructor con = null;     //主要就是这句了
        try {
            con = cls.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            baseEduCrawler = (BaseEduCrawler)con.newInstance(params);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        baseEduCrawler.setConf(conf);
        baseEduCrawler.setResumable(true);                          //设置为断点式爬取

        baseEduCrawler.addSeed(crawlDatums, true);                        //添加seeds
        baseEduCrawler.addSeed(baseEduNextPageCrawler.getSeeds(),true);

        regs = conf.getValues(suffix+".crawler.reg.*");
        baseEduCrawler.addRegexs(regs);

        baseEduCrawler.setThreads(50);                              //设置线程数
        baseEduCrawler.setTopN(1000);                               //设置每一个segment爬取网页的最大上限
        if(isComments){

            BaseEduParseComment baseEduParseComment=null;
            try {
                cls = Class.forName(parseCommentsClassName);
            } catch (ClassNotFoundException e) {
                LOG.info("Initial the BaseEduParseComment class failed!\r\n" +
                        "Please make sure that the class name contains the absolute package path.\r\n"+
                        "The current class name is :"+parseCommentsClassName);
                return ;
            }
            paramTypes = new Class[]{Configuration.class};
            params = new Object[]{conf}; // 方法传入的参数
            try {
                con = cls.getConstructor(paramTypes);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                baseEduParseComment = (BaseEduParseComment)con.newInstance(params);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            baseEduParseComment.setConf(conf);
            baseEduCrawler.setCrawlComments(this.isComments);           //设置是否爬取评论
            baseEduCrawler.setBaseEduParseComment(baseEduParseComment);
        }
        try {
            baseEduCrawler.start(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
