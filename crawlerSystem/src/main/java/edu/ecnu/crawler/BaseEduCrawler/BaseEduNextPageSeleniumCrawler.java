package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/31/15.
 */
public class BaseEduNextPageSeleniumCrawler extends BreadthCrawler {

    public BaseEduNextPageSeleniumCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        return new SeleniumHttpRequest().getResponse(crawlDatum);
    }


    @Override
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
    }

    @Override
    public void afterVisit(Page page, CrawlDatums next) {
        super.afterVisit(page, next);
        if (next==null || next.size() == 0) {
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



    protected Element getNextPageElement(Page page) {
        String suffix = conf.get("crawler.suffix");
        String nextSelector = conf.get(suffix + ".next.nextpage.selector");
        String nextContent = conf.get(suffix + ".next.nextpage.nextcontent");
        if(nextContent==null){
            nextContent = "下一页"; //如果在配置文件中没有配置，则默认为“下一页”
        }
        Elements elements = page.select(nextSelector);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if (element.toString().contains(nextContent)) {
                return element;
            }
        }
        return null;
    }

}
