package edu.ecnu.crawler.GansuEdu;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public class GsNextPageCrawler extends BaseEduNextPageBreadthCrawler {

    public GsNextPageCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public Element getNextPageElement(Page page) {
        String nextSelector = conf.get(suffix + ".next.nextpage.selector");
        Element element= page.select(nextSelector).first();   //下一页存在于a标签的title属性内
        return element;
    }
}
