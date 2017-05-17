package cn.edu.hfut.dmic.webcollector.example;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import org.jsoup.select.Elements;

/**
 * Created by wlcheng on 12/31/15.
 */
public class DemoSelenium extends BreadthCrawler {

    public DemoSelenium(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        return new DemoSeleniumHttpRequest().getResponse(crawlDatum);
    }
    
    @Override
    public void visit(Page page, CrawlDatums next) {
        Elements nextElement = page.select("td[height=30] a");
        String nextUrl = nextElement.get(0).absUrl("href");
        System.out.println(nextUrl);
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        DemoSelenium crawler = new DemoSelenium("test",true);
        crawler.addSeed("http://jzb.com/zixun/zk-sh/");
        crawler.start(1);
    }
}
