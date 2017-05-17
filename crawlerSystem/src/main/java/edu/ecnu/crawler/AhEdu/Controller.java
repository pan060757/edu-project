package edu.ecnu.crawler.AhEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import edu.ecnu.crawler.ChinaEdu.ChinaEduCrawler;
import edu.ecnu.socket.SocketCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Controller extends ControllerTemplate{

    private static SocketCommunication sc;
    private static ResourceBundle rb;
    private static String ip;
    public Controller(Configuration conf) {
        super(conf);
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip,4700,19);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-ahedu.xml");
        Controller controller = new Controller(conf);
        String suffix = conf.get("crawler.suffix");
        String publisher=conf.get("ahedu.publisher");
        String sourceId=conf.get("ahedu.sourceId");
        sc.sendMessage(sourceId +"\t" + publisher + "\t" + "start");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.AhEdu.AhEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.AhEdu.AhEduNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId+ "\t" + publisher+"\t"+"stop");
    }

//    public static void main(String[] args){
//
//        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot+"/conf/webcollector-ahedu.xml");
//        Controller controller = new Controller(conf);
//        controller.setEduCrawlerClassName("edu.ecnu.crawler.AhEdu.AhEduCrawler");
//        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.AhEdu.AhEduNextPageCrawler");
//        controller.run();
//
///*
//        String suffix = conf.get("crawler.suffix");
//        String crawldb = conf.get(suffix+".crawldb");      //第一步:加载配置的crawldb,并生成对应的NextPageCrawler
//        String crawlnextdb = crawldb+".next";
//        BaseEduNextPageBreadthCrawler nextPageCrawler = new AhEduNextPageCrawler(crawlnextdb,true);
//
//
//        nextPageCrawler.setConf(conf);
//        /***
//         *
//        nextPageCrawler.setResumable(true);     //设置为断点式爬取
//         */
//
//        /*
//        nextPageCrawler.setConf(conf); //设置为断点式爬取
//
//
//        nextPageCrawler.setResumable(true);
//
//        CrawlDatums crawlDatums = new CrawlDatums();
//
//
//
//        List<String> urls = conf.getValues(suffix+".seeds.*");       //第二步:加载配置的seed
//        crawlDatums.add(urls);
//        nextPageCrawler.addSeed(crawlDatums);
//
//
//        List<String> regs = conf.getValues(suffix+".next.reg.*");      //第三步:加载配置的正则表达式
//        nextPageCrawler.addRegexs(regs);
//
//        nextPageCrawler.start(Config.MAX_DEPTH);
//
//        nextPageCrawler.stop();
//        LOG.info("Finish crawl all the next pages. The total next pages' count is :" + nextPageCrawler.seeds.size());
//
//
//        /**********************************************/
//
//       /*
//        BaseEduCrawler ahEduCrawler = new AhEduCrawler(crawldb,true);
//
//
//        ahEduCrawler.setConf(conf);
//
//        ahEduCrawler.setResumable(true);  //设置为断点式爬取
//        ahEduCrawler.addSeed(crawlDatums);
//        ahEduCrawler.addSeed(nextPageCrawler.seeds);
//
//        regs = conf.getValues(suffix+".crawler.reg.*");             //第五步:为page crawler加载对应的正则表达式
//        ahEduCrawler.addRegexs(regs);
//
//
//        ahEduCrawler.setResumable(true);
//        ahEduCrawler.setThreads(50);
//        ahEduCrawler.start(2);
//        */
//    }
}
