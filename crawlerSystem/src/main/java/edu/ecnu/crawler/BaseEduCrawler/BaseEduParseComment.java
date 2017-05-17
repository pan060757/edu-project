package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import cn.edu.hfut.dmic.webcollector.crawler.Crawler;
import cn.edu.hfut.dmic.webcollector.util.Configuration;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by song on 2015/12/16.
 */
public abstract class BaseEduParseComment {


    protected Configuration conf = null;

    protected List<String> commentJsons = new ArrayList<String>();

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    protected static Crawler crawler = new Crawler();
    public List<String> getCommentJsons() {
        return commentJsons;
    }

    public void setCommentJsons(List<String> commentJsons) {
        this.commentJsons = commentJsons;
    }

    protected String reformat(String message) {
        StringBuilder sb = new StringBuilder("");
        try {
            byte[] utf8 = message.getBytes("UTF-8");
            message = new String(utf8,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        message = message.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&apos;").replace("^", "").replace("\b", "");
        for (int i = 0; i < message.length(); i++) {
            char ch = message.charAt(i);
            if ((ch == 0x9) || (ch == 0xA) || (ch == 0xD)
                    || ((ch >= 0x20) && (ch <= 0xD7FF))
                    || ((ch >= 0xE000) && (ch <= 0xFFFD))
                    || ((ch >= 0x10000) && (ch <= 0x10FFFF)))
                sb.append(ch);
        }
        return sb.toString();
    }


    protected String parseUnixTime(String unixTime){
        try{
            Long timestamp = Long.parseLong(unixTime);
            if(unixTime.length()==10){
                timestamp=timestamp*1000;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        }catch (NumberFormatException e){
            return unixTime;
        }
    }

    protected abstract ArrayList<Comment> getComments(NewsPage newsPage);



    protected abstract String tryCommentGet(String url,String page);


    public String crawlPage(String url) {
        crawler.setUrlnoCheck(url.trim());
        String content = "";
        int trycount=6;
        try {
            content = crawler.getContent();
            while(content.length()<50 && trycount>0){
                sleep();
                trycount--;
                content = crawler.getContent();
            }
            sleep();
        }catch (Exception e){
            e.printStackTrace();
        }

        return content;
    }


    protected String crawlPage(String url,String encoding) {
        crawler.setUrlnoCheck(url.trim());
        String content = "";
        int trycount=5;
        try {
            content = crawler.getContent(encoding);
            while(content.length()<50 && trycount>0){
                sleep();
                trycount--;
                content = crawler.getContent();
            }
            sleep();
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    protected void sleep() {
        int chance = new Random().nextInt(20);
        if(chance==0){
            long sleepTime = System.currentTimeMillis() % 3500 + 1000;
            System.out.println("Sleep " + sleepTime + "ms");
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
