package edu.ecnu.crawler.XjEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Controller extends ControllerTemplate {

    public Controller(Configuration conf) {
        super(conf);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }


    public static void main(String[] args) {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-xjedu.xml");
        Controller controller = new Controller(conf);
        controller.setEduCrawlerClassName("edu.ecnu.crawler.XjEdu.XjEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.XjEdu.XjNextPageCrawler");
        controller.run();
    }

//    public static void main(String[] args) throws Exception {
//
//        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot+"/conf/webcollector-xjedu.xml");
//        String suffix = conf.get("crawler.suffix");
//        String crawldb = conf.get(suffix+".crawldb");      //第一步:加载配置的crawldb,并生成对应的NextPageCrawler
//        BaseEduNextPageBreadthCrawler nextPageCrawler = new XjNextPageCrawler(crawldb,true);
//
//        nextPageCrawler.setConf(conf);
//
//        /***
//         *
//        nextPageCrawler.setResumable(true);     //设置为断点式爬取
//         */
//        CrawlDatums crawlDatums = new CrawlDatums();
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
//        LOG.info("Finish crawl all the next pages. The total next pages' count is :" + nextPageCrawler.seeds.size());
//
//        /**********************************************/
//        BaseEduCrawler EduCrawler = new XjEduCrawler(crawldb,true);
//
//        EduCrawler.setConf(conf);
//        /***
//         *
//         ahEduCrawler.setResumable(true);     //设置为断点式爬取
//         */
//        EduCrawler.addSeed(crawlDatums);
//        EduCrawler.addSeed(nextPageCrawler.seeds);
//
//        regs = conf.getValues(suffix+".crawler.reg.*");             //第五步:为page crawler加载对应的正则表达式
//        EduCrawler.addRegexs(regs);
//
//        EduCrawler.start(2);
//    }
}
