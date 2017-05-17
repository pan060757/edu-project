package cn.edu.hfut.dmic.webcollector.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import static org.junit.Assert.assertEquals;

/**
 * Created by song on 2015/12/16.
 */
public class Test {
    public static void main(String args[]) {
            Crawler crawler = new Crawler();
            crawler.setUrlnoCheck("http://jzb.com/zixun/zk-sh/");
            String content = crawler.getContent("gb2312");

            //解析文章列表页面，获取需要爬取页面URL
            Document doc = Jsoup.parse(content);
            System.out.println(doc.toString());
    }
}
