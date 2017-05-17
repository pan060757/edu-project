package edu.ecnu.crawler.JybEdu;

import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageRamCrawler;

/**
 * Created by wlcheng on 12/7/15.
 */
public class JybNextPageCrawler extends BaseEduNextPageRamCrawler {
    public  JybNextPageCrawler(String crawlPath, boolean autoParse) {
        super(autoParse);
    }
}
