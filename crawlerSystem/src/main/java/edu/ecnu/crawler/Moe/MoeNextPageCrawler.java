package edu.ecnu.crawler.Moe;

import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageRamCrawler;

/**
 * Created by wlcheng on 12/7/15.
 */
public class MoeNextPageCrawler extends BaseEduNextPageRamCrawler {
    public MoeNextPageCrawler(String crawlPath, boolean autoParse) {
        super(autoParse);
    }
}
