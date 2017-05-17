package edu.ecnu.crawler.JzbEdu;

import cn.edu.hfut.dmic.webcollector.example.DemoSeleniumHttpRequest;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduNextPageBreadthCrawler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.URL;

/**
 * Created by wlcheng on 12/7/15.
 */
public class JzbNextPageCrawler extends BaseEduNextPageBreadthCrawler {


    public JzbNextPageCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
    }

    @Override
    public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
        WebDriver driver=new HtmlUnitDriver();
        try {
            driver.get(crawlDatum.getUrl());
            HttpResponse response = new HttpResponse(new URL(crawlDatum.getUrl()));
            response.setCode(200);
            String html = driver.getPageSource();
            response.setHtml(html);
            response.addHeader("Content-Type", "text/html");
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
