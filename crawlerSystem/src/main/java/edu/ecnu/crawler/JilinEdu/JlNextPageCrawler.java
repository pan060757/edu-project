package edu.ecnu.crawler.JilinEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public class JlNextPageCrawler extends BaseEduNextPageBreadthCrawler {


    public JlNextPageCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }
}
