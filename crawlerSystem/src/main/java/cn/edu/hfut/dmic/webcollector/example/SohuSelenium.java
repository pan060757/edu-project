package cn.edu.hfut.dmic.webcollector.example;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import org.jsoup.nodes.Element;

/**
 * Created by song on 2015/12/17.
 */
public class SohuSelenium {
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        return new DemoSeleniumHttpRequest().getResponse(crawlDatum);
    }

    public void visit(Page page, CrawlDatums next) {
        //span#outlink1表示网页中id为outlink1的span元素
        //"span#outlink1"中的信息是由javascript加载的
        System.out.println("反链数:" + page.select("div[style=margin-top:5px;] a").attr("href"));
        Element nextElement = page.select("div[style=margin-top:5px;] a").first();
        String nextUrl = nextElement.absUrl("href");
        System.out.println(nextUrl);
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        DemoSeleniumCrawler crawler = new DemoSeleniumCrawler();
        crawler.addSeed("http://learning.sohu.com/liuxue/");
//        crawler.addSeed("http://paper.dxy.cn/tag/write");
        crawler.start(1);
    }
}
