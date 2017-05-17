package edu.ecnu.crawler.GczEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import cn.edu.hfut.dmic.webcollector.util.FileUtils;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.ControllerTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wlcheng on 12/7/15.
 */
public class Controller{


    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration(edu.ecnu.util.Config.crawlerRoot +"/conf/webcollector-gczedu.xml");
        String suffix = conf.get("crawler.suffix");
        String crawldb = conf.get(suffix + ".crawldb");      //第一步:加载配置的crawldb,并生成对应的NextPageCrawler

        CrawlDatums crawlDatums = new CrawlDatums();

        String seedPath = conf.get(suffix + ".path.seeds*");;
        List<String> urls = null;                                       //第二步:加载配置的seed
        try {
            urls = FileUtils.readFileAsList(edu.ecnu.util.Config.crawlerRoot + File.separator + seedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        crawlDatums.add(urls);
        List<String> regs = conf.getValues(suffix+".crawler.reg.*");      //第三步:加载配置的正则表达式

        /**********************************************/
        BaseEduCrawler EduCrawler = new GczEduCrawler(crawldb,true);
        EduCrawler.setConf(conf);
        EduCrawler.setResumable(true);
        EduCrawler.addSeed(crawlDatums,true);

        EduCrawler.addRegexs(regs);

        EduCrawler.start(2000);
    }
}
