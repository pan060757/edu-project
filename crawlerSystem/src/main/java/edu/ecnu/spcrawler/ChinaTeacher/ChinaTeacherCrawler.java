package edu.ecnu.spcrawler.ChinaTeacher;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.ecnu.spcrawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.storage.InfoRecoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by song on 2015/12/20.
 */
public class ChinaTeacherCrawler extends BaseEduCrawler {
    static ArrayList<String> urls = new ArrayList<String>();
    private static final String cur_year = "2016";
    public void crawlUrlList() {
        String url = "";
        for (int i =1; i <60; i++) {
            int pagenum=0;
            url = "http://paper.chinateacher.com.cn/zgjsb/html/"+date(i)+"/node_22.htm#";
            String str= "http://paper.chinateacher.com.cn/zgjsb/html/"+date(i);
            System.out.println(url);
            System.out.println("开始爬取page:" + url);
            pagenum++;
            WebClient wc = new WebClient();
            wc.getOptions().setJavaScriptEnabled(false);
            wc.getOptions().setCssEnabled(false);
            wc.getOptions().setThrowExceptionOnScriptError(true);
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            wc.getOptions().setTimeout(10000);
            HtmlPage page = null;
            try {
                page = wc.getPage(url);
                getUrlList(page, url,str, pagenum);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    public void getUrlList(HtmlPage page,String linkUrl,String str,int pagenum) throws InterruptedException, IOException{
        String pageXml = page.asXml();
        Document doc = Jsoup.parse(pageXml);
        Elements eles = doc.select("div.fint2 td.default");
        String url="";
        for (Element ele : eles) {
            Element href=ele.select("a[href]").first();
            url =str+href.attr("href");
            urls.add(url);
        }
    }


    //爬取页面
    public void crawlPage(ArrayList<String> urlList,String publisher)throws Exception
    {
        for(String url:urlList) {
            System.out.println(url);
            NewsPage news = ContentExtractor.getNewsByUrl(url);
            news.setPublisher(publisher);
            news.setPageId(String.valueOf(System.currentTimeMillis()));
            news.setSourceId("52");
            InfoRecoder.save(news);
            cnt++;
        }
    }
    public String date(int a) {
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-01/0" + a;
            return cur_year + "-01/" + a;
        }
        a = a - 31;
        if (a <= 28) {
            if (a < 10)
                return cur_year + "-02/0" + a;

            return cur_year + "-02/" + a;
        }
        a = a - 28;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-03/0" + a;
            return cur_year + "-03/" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-04/0" + a;
            return cur_year + "-04/" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-05/0" + a;
            return cur_year + "-05/" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-06/0" + a;
            return cur_year + "-06/" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-07/0" + a;
            return cur_year + "-07/" + a;
        }
        a = a - 31;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-08/0" + a;
            return cur_year + "-08/" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-09/0" + a;
            return cur_year + "-09/" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-10/0" + a;
            return cur_year + "-10/" + a;
        }
        a = a - 31;
        if (a <= 30) {
            if (a < 10)
                return cur_year + "-11/0" + a;
            return cur_year + "-11/" + a;
        }
        a = a - 30;
        if (a <= 31) {
            if (a < 10)
                return cur_year + "-12/0" + a;
            return cur_year + "-12/" + a;
        } else
            return "";
    }
    public static void main(String[] args) throws Exception {
        ChinaTeacherCrawler crawler = new ChinaTeacherCrawler();
        crawler.crawlUrlList();
        crawler.crawlPage(urls, "中国教师报");
    }
}
