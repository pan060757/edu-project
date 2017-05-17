package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/7/15.
 */
public abstract class BaseEduNextPageBreadthCrawler extends BreadthCrawler {

    protected String suffix = "";
    protected BaseEduNextPageBreadthCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }


    public void visit(Page page, CrawlDatums next) {
        System.out.println("visiting:" + page.getUrl() + "\tdepth=" + page.getMetaData("depth"));
        Element nextElement = getNextPageElement(page);
        if(nextElement==null) return;
        String nextUrl = nextElement.absUrl("href");
        if(nextUrl==null) return;
        if(nextUrl.equals(page.getUrl())) return;
        CrawlDatum crawlDatum = new CrawlDatum(nextUrl).putMetaData("depth", "1");
        next.add(crawlDatum);
        seeds.add(crawlDatum);
        //如果进来的是新闻页面
    }

    @Override
    public void afterVisit(Page page, CrawlDatums next) {
        super.afterVisit(page, next);
        if (next.size() == 0) {
            return;
        }
        int depth;
        //如果在添加种子时忘记添加depth信息，可以通过这种方式保证程序不出错
        if (page.getMetaData("depth") == null) {
            depth = 1;
        } else {
            depth = Integer.valueOf(page.getMetaData("depth"));
        }
        depth++;
        for (CrawlDatum datum : next) {
            datum.putMetaData("depth", depth + "");
        }
    }

    public Element getNextPageElement(Page page) {
        String  suffix = conf.get("crawler.suffix");
        String nextSelector = conf.get(suffix+".next.nextpage.selector");
        Elements elements = page.select(nextSelector);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element.text().contains("下一页") ||
                    element.text().contains(">") ) {
                return element;
            }
        }
        return null;
    }

}
