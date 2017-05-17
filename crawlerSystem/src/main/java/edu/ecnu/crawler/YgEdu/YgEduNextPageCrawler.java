package edu.ecnu.crawler.YgEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageRamCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public class YgEduNextPageCrawler extends BaseEduNextPageRamCrawler {
    public YgEduNextPageCrawler(String crawlPath, boolean autoParse) {
        super(autoParse);
    }
}
