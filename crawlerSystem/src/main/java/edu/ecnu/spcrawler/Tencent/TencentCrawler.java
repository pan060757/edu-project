package edu.ecnu.spcrawler.Tencent;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.ecnu.socket.SocketCommunication;
import edu.ecnu.spcrawler.BaseEduCrawler.BaseEduCrawler;
import edu.ecnu.storage.InfoRecoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by song on 2015/12/20.
 */
public class TencentCrawler extends BaseEduCrawler implements  Runnable{
    private ArrayList<String> urls = new ArrayList<String>();
    private static SocketCommunication sc;
    private static ResourceBundle rb;
    private static String ip;
    private int s = 0;
    private int e = 0;

    public TencentCrawler(int start, int end) {
        this.s = start;
        this.e = end;
        rb = ResourceBundle.getBundle("db");
        ip = rb.getString("ipAdd");
        sc = new SocketCommunication(ip,4700,32);
    }
    public void crawlUrlList() {
        String url = "";
        for (int i = s; i < e; i++) {
            url = "http://roll.edu.qq.com/index.htm?site=edu&mod=1&date="+date(i) + "&cata=";
            WebClient wc = new WebClient();
            wc.getOptions().setJavaScriptEnabled(true);
            wc.getOptions().setCssEnabled(false);
            wc.getOptions().setThrowExceptionOnScriptError(true);
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            wc.getOptions().setTimeout(10000);
            try {
                HtmlPage page = wc.getPage(url);
                getUrlList(page);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //获取UrlList
    public void getUrlList(HtmlPage page){
        for (int j = 0; j < 50; j++) {
            if (page != null && page.asXml().contains("mainCon")) {
                break;
            }
            try {
                page.wait(1000);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        String pageXml = page.asXml();
        Document doc = Jsoup.parse(pageXml, "http://roll.edu.qq.com");
        Elements eles = doc.select("div.list li");

        for (Element ele : eles) {
            String url = ele.select("a[href]").first().attr("href");
            urls.add(url);
            WebClient wc = new WebClient();
            wc.getOptions().setJavaScriptEnabled(true);
            wc.getOptions().setCssEnabled(false);
            wc.getOptions().setThrowExceptionOnScriptError(true);
            wc.setAjaxController(new NicelyResynchronizingAjaxController());
            wc.getOptions().setTimeout(10000);
            System.out.println("Get url " + url);

        }
        HtmlAnchor nextpage = null;
        try {
            nextpage = page.getAnchorByText("下一页>");
            if (nextpage != null) {
                try {
                    HtmlPage nextone = nextpage.click();
                    System.out.println("Next page...");
                    getUrlList(nextone);
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch (ElementNotFoundException e) {
            System.out.println("Not found next page! ");
        }
    }

   /***
    *爬取文章页面以及评论内容
    */
    public void crawlPage(ArrayList<String> urlList,String publisher)
    {
        for(String url:urlList) {
            System.out.println(url);
            try {
                NewsPage newsPage = ContentExtractor.getNewsByUrl(url);
                newsPage.setPublisher(publisher);
                newsPage.setUrl(url);
                TencentParseComments tencent = new TencentParseComments();
                ArrayList<Comment> comments=tencent.getComments(newsPage);
                newsPage.setComments(comments);
                newsPage.setComments_source(tencent.getCommentJsons());
                newsPage.setPageId(String.valueOf(System.currentTimeMillis()));
                newsPage.setSourceId("32");
                InfoRecoder.save(newsPage);
            }catch(Exception e)
            {
                e.printStackTrace();
                continue;
            }
        }
    }


    public static void main(String[] args){
        int threadNumber = 10;
        int step = 366 / threadNumber;
        Thread[] threads = new Thread[threadNumber];
        int j = 0;
        for (int i = 0; i <366; i = i+step) {
            int end = i+step < 366 ? i+step:366;
            threads[j] = new Thread( new TencentCrawler(i,end));
            threads[j].start();
        }
    }

    @Override
    public void run() {
        try {
            crawlUrlList();
            crawlPage(urls, "腾讯教育");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
