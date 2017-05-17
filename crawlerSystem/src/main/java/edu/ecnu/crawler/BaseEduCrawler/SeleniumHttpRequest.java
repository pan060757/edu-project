package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.URL;

/**
 * Created by wlcheng on 12/18/15.
 */
public class SeleniumHttpRequest {
    static {
        Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
        logger.setLevel(Level.OFF);
    }
    public HttpResponse getResponse(CrawlDatum datum) throws Exception {
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        try {
            driver.get(datum.getUrl());
            HttpResponse response = new HttpResponse(new URL(datum.getUrl()));
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
