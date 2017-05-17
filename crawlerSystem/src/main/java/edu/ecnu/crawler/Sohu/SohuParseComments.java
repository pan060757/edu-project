package edu.ecnu.crawler.Sohu;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduParseComment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by song on 2015/12/16.
 */
public class SohuParseComments extends BaseEduParseComment {

    int cmt_sum=0;

    public SohuParseComments(Configuration conf){
        this.conf = conf;
    }

    public ArrayList<Comment> getComments(String url){
        return null;
    }

    @Override
    public ArrayList<Comment> getComments(NewsPage newsPage) {
        String url = newsPage.getUrl();
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String topicID = getTopicID(url);

        String content = tryCommentGet(topicID);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("listData");

        if(jsonObject!=null && jsonObject.containsKey("cmt_sum")){
            cmt_sum = jsonObject.getInt("cmt_sum");
        }

        JSONArray array;
        if(jsonObject!=null && jsonObject.containsKey("comments")){
            array=jsonObject.getJSONArray("comments");
        }
        else{
            return comments;
        }

        Iterator<JSONObject> iter = array.iterator();
        commentJsons.clear();
        while(iter.hasNext()){
            JSONObject comment = iter.next();
            String text="",user_id="",user_gender="0",user_region="",time="",user_ip="",comment_id="";
            double score;
            int support,against,replyCount;

            if(comment.containsKey("content")){
                text =reformat(comment.getString("content"));
            }
            if(comment.containsKey("create_time")){
                time =parseUnixTime(comment.getString("create_time"));
            }
            if(comment.containsKey("user_id")){
                user_id=comment.getString("user_id");
            }
            if(comment.containsKey("ip")){
                user_ip=comment.getString("ip");
            }
            if(comment.containsKey("ip_location")){
                user_region = stripHtml(comment.getString("ip_location"));
            }
            if(comment.containsKey("comment_id")){
                comment_id = comment.getString("comment_id");
            }
            if(comment.containsKey("score")){
                score = Double.parseDouble(comment.getString("score"));
            }

            if(comment.containsKey("support_count")){
                support= Integer.parseInt(comment.getString("support_count"));
            }
            if(comment.containsKey("oppose_count")){
                against= Integer.parseInt(comment.getString("oppose_count"));
            }
            if(comment.containsKey("reply_count")){
                replyCount=Integer.parseInt(comment.getString("reply_count"));
            }

            comment.accumulate("pageId", newsPage.getPageId());
            comment.accumulate("pageUrl",newsPage.getUrl());
            commentJsons.add(comment.toString());
            Comment cmt = new Comment(newsPage.getPageId(),null,null,user_region,text);
            cmt.setTime(time);
            cmt.setIp(user_ip);
            comments.add(cmt);
        }
        setCommentJsons(commentJsons);
        return comments;
    }

    @Override
    public String tryCommentGet(String url, String page) {
        return null;
    }

    public String getTopicID(String url) {
        String last = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf('.'));
        last = last.replace("n","");
        return last;
    }

    public String stripHtml(String text){
        return text.replaceAll("<[^>]*>","");
    }

    public String tryCommentGet(String cmt_id){
        String url="http://changyan.sohu.com/node/html?t="+System.currentTimeMillis()+
                "callback=fn&appid=cyqemw6s1&client_id=cyqemw6s1&page_size=100&topicsid=" +cmt_id;
        String content = crawlPage(url, "UTF-8");
        return content;
    }

    public static void main(String args[])
    {
        SohuParseComments sohu= new SohuParseComments(new Configuration("crawlerSystem/conf/webcollector-sohu.xml"));
        NewsPage newsPage = new NewsPage();
        newsPage.setUrl("http://learning.sohu.com/20151208/n430243904.shtml");
        ArrayList<Comment> comments=sohu.getComments(newsPage);
        for(Comment comment:comments)
        {
            System.out.println(comment.getIp()+"\t"+comment.getTime()+"\t"+comment.getLocation()+"\t"+comment.getGender()+"\t"+comment.getTime()+"\t"+comment.getContent());
        }
    }
}
