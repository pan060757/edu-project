package edu.ecnu.spcrawler.Sciencenet;

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
public class SciencenetCrawler extends BaseEduCrawler {
    static ArrayList<String> urls = new ArrayList<String>();

    public void crawlUrlList() {
        String url = "";
        for (int i =1; i <366; i++) {
            int pagenum=0;
            url = "http://news.sciencenet.cn/todaynews.aspx?d=" +date(i);
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
                getUrlList(page, url, pagenum);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void getUrlList(HtmlPage page,String linkUrl,int pagenum) throws InterruptedException, IOException{
        String pageXml = page.asXml();
        Document doc = Jsoup.parse(pageXml);

        Elements eles = doc.select("table[border=0][cellpadding=0][cellspacing=0][width=100%] tr");
        String url="";
        for (Element ele : eles) {
            Elements td = ele.select("td");
            if (td.size()>2) {
                Elements href = ele.select("td[align=left] a[href]");
                if( href.attr("href").startsWith("http"))
                    url =href.attr("href");
                else
                    url ="http://news.sciencenet.cn" + href.attr("href");
                Element dateDiv = ele.select("td").get(2);
                if (dateDiv.text().contains("2015")) {
                    try {
                        urls.add(url);
                    }catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    return;
                }
            }
        }
        Element pageDiv = doc.select("div#AspNetPager1").select("b").get(1);
        int pageCount=Integer.parseInt(pageDiv.text());
        String nextHref="";
        if(pagenum<pageCount)
        {
            pagenum++;
            nextHref=linkUrl.substring(0,linkUrl.lastIndexOf("."))+"-"+String.valueOf(pagenum)+linkUrl.substring(linkUrl.lastIndexOf("."));
        }
        else
        {
            return;
        }
        WebClient wc = new WebClient();
        wc.getOptions().setJavaScriptEnabled(false);
        wc.getOptions().setCssEnabled(false);
        wc.getOptions().setThrowExceptionOnScriptError(true);
        wc.setAjaxController(new NicelyResynchronizingAjaxController());
        wc.getOptions().setTimeout(10000);
        HtmlPage nextpage = wc.getPage(nextHref);
        System.out.println("开始爬取:"+nextHref);
        getUrlList(nextpage, nextHref, pagenum);
    }

    //爬取页面
    public void crawlPage(ArrayList<String> urlList,String publisher)throws Exception
    {
        for(String url:urlList) {
            System.out.println(url);
            NewsPage news = ContentExtractor.getNewsByUrl(url);
            news.setPublisher(publisher);
            news.setPageId(String.valueOf(System.currentTimeMillis()));
            news.setSourceId("62");
            InfoRecoder.save(news);
            cnt++;
        }
    }
    public static void main(String[] args) throws Exception {
        SciencenetCrawler crawler = new SciencenetCrawler();
        crawler.crawlUrlList();
        crawler.crawlPage(urls, "科学网博客");
    }
}
