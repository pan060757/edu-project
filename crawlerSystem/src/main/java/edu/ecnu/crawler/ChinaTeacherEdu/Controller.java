package edu.ecnu.crawler.ChinaTeacherEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;
import edu.ecnu.socket.SocketCommunication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Controller extends ControllerTemplate {
    private static SocketCommunication sc;
    private static ResourceBundle rb;
    private static String ip;
    public Controller(Configuration conf) {
        super(conf);
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip,4700,52);
    }

    public Controller(Configuration conf, boolean isComments) {
        super(conf, isComments);
    }


    public static void main(String[] args) {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot + "/conf/webcollector-chinateacher.xml");
        Controller controller = new Controller(conf);
        String suffix = conf.get("crawler.suffix");
        String publisher=conf.get(suffix+".publisher");
        String sourceId=conf.get(suffix+".sourceId");
        controller.setEduCrawlerClassName("edu.ecnu.crawler.ChinaTeacherEdu.ChinaTeacherEduCrawler");
        controller.setNextPageCrawlerClassName("edu.ecnu.crawler.ChinaTeacherEdu.ChinaTeacherEduNextPageCrawler");
        controller.run();
        sc.sendMessage(sourceId+ "\t" + publisher+"\t"+"stop");
    }

//    public static void main(String[] args) throws Exception {
//
//        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot+"/conf/webcollector-chinateacher.xml");
//        String suffix = conf.get("crawler.suffix");
//        String crawldb = conf.get(suffix+".crawldb");      //第一步:加载配置的crawldb,并生成对应的NextPageCrawler
//        BaseEduNextPageBreadthCrawler nextPageCrawler = new ChinaTeacherEduNextPageCrawler(crawldb,true);
//
//        nextPageCrawler.setConf(conf);
//        /***
//         *
//         nextPageCrawler.setResumable(true);     //设置为断点式爬取
//         */
//
//        CrawlDatums crawlDatums = new CrawlDatums();
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
//
//        LOG.info("Finish crawl all the next pages. The total next pages' count is :" + nextPageCrawler.seeds.size());
//
//
//        /**********************************************/
//        BaseEduCrawler ahEduCrawler = new ChinaTeacherEduCrawler(crawldb,true);
//
//
//        ahEduCrawler.setConf(conf);
//        /***
//         *
//         ahEduCrawler.setResumable(true);     //设置为断点式爬取
//         */
//
//        ahEduCrawler.addSeed(crawlDatums);
//        ahEduCrawler.addSeed(nextPageCrawler.seeds);
//
//
//
//        regs = conf.getValues(suffix+".crawler.reg.*");             //第五步:为page crawler加载对应的正则表达式
//        ahEduCrawler.addRegexs(regs);
//
//        ahEduCrawler.start(2);
//    }
}
