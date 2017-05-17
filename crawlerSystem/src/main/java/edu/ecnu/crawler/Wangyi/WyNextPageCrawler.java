package edu.ecnu.crawler.Wangyi;

import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public class WyNextPageCrawler extends BaseEduNextPageBreadthCrawler {

    public WyNextPageCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }


    public Element getNextPageElement(Document doc) {
        String nextSelector = conf.get(suffix + ".next.nextpage.selector");
        Elements elements = doc.select(nextSelector);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element.text().contains(">")) {
                return element;
            }
        }
        return null;
    }
}
