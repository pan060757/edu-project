package edu.ecnu.crawler.HaEdu;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public class HaEduNextPageCrawler extends BaseEduNextPageBreadthCrawler {

    public HaEduNextPageCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        System.out.println("visiting:" + page.getUrl() + "\tdepth=" + page.getMetaData("depth"));
        Document doc = page.getDoc();
        Element nextElement = getNextPageElement(doc,page.getUrl());
        if(nextElement==null) return;
        String nextUrl = nextElement.absUrl("href");
        if(nextUrl==null) return;
        CrawlDatum crawlDatum = new CrawlDatum(nextUrl).putMetaData("depth", "1");
        next.add(crawlDatum);
        seeds.add(crawlDatum);
        //如果进来的是新闻页面
    }

    public Element getNextPageElement(Document doc,String url) {
        String nextSelector = conf.get(suffix+".next.nextpage.selector");
        Elements elements = doc.select(nextSelector);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String nextUrl=element.attr("abs:href");
            if(nextUrl.compareTo(url)>0)
            {
                return element;
            }
        }
        return null;
    }
}
