package edu.ecnu.crawler.BaseEduCrawler;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.socket.SocketCommunication;
import edu.ecnu.storage.InfoRecoder;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public abstract  class BaseEduCrawler  extends BreadthCrawler {
    Configuration conf = null;
    String suffix = "";
    String crawldb="";
    private static String ip;
    private static SocketCommunication sc;
    private static ResourceBundle rb;

    BaseEduParseComment baseEduParseComment = null;

    public void setBaseEduParseComment(BaseEduParseComment baseEduParseComment) {
        this.baseEduParseComment = baseEduParseComment;
    }


    public BaseEduCrawler(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.crawldb=crawlPath;
        sc = new SocketCommunication(ip,4700,19);
    }


    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
        suffix = conf.get("crawler.suffix");
    }

    public void visit(Page page, CrawlDatums next) {
        System.out.println("visiting:" + page.getUrl() + "\tdepth=" + page.getMetaData("depth"));
        String url = page.getUrl();

        //如果进来的是新闻页面
        if(isNewsPage(url)){
                NewsPage newsPage= ContentExtractor.getNewsByUrl(conf,url);
                if (newsPage == null) {
                    return;
                }
                if (crawlComments) {
                    List<Comment> comments = baseEduParseComment.getComments(newsPage);
                    if (comments != null && comments.size() != 0) {
                        newsPage.setComments(comments);
                    }
                    List<String> comments_source = baseEduParseComment.getCommentJsons();
                    if (comments_source != null && comments_source.size() != 0) {
                        newsPage.setComments_source(comments_source);
                    }
                }
                InfoRecoder.save(newsPage);

                //与服务器端socket通信
                String content = "";
                if (newsPage.getContent().length() > 50) {
                    content = newsPage.getContent().substring(0, 50);
                } else {
                    content = newsPage.getContent();
                }
                String message = newsPage.getSourceId()+"\t"+ newsPage.getPublisher() + "\t" + newsPage.getUrl() + "\t" + newsPage.getTitle() + "\t" + newsPage.getTime() + "\t" + content + "\t" + "1";
                if(message!=null)
                sc.sendMessage(message);

        }else{ //如果进来的不是新闻页面那么就到afterVisit中进行解析，得到该页面的所有的links
            next.add(new CrawlDatum(page.getUrl()));
            this.addSeed(new CrawlDatum(page.getUrl()),true);
        }
    }

    @Override
    public void afterVisit(Page page, CrawlDatums next) {
        super.afterVisit(page, next);
        //当前页面的depth为x，则从当前页面解析的后续任务的depth为x+1
        int depth;
        //如果在添加种子时忘记添加depth信息，可以通过这种方式保证程序不出错
        if(page.getMetaData("depth")==null){
            depth=1;
        }else{
            depth=Integer.valueOf(page.getMetaData("depth"));
        }
        depth++;
        for(CrawlDatum datum:next){
            if(datum.getMetaData().size()==0 ){
                datum.putMetaData("depth", depth+"");
            }
        }
    }

    public boolean isNewsPage(String url){

        List<String> regs = conf.getValues(suffix+".crawler.reg.*");
        for (int i = 0; i < regs.size(); i++) {
            if(url.matches(regs.get(i))){
                return true;
            }
        }
        return false;
    }
}
