package edu.ecnu.crawler.Wangyi;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduParseComment;
import net.sf.json.JSONObject;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by song on 2015/12/16.
 */
public class WangyiParseComments extends BaseEduParseComment {

    public WangyiParseComments(Configuration conf){
        this.conf = conf;
    }

    @Override
    public ArrayList<Comment> getComments(NewsPage newsPage) {
        String url = newsPage.getUrl();
        String pageId = newsPage.getPageId();
        System.out.println("Add comments for " + url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String urlId = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        String content = tryCommentGet(urlId, null);
        try {
            JSONObject jsonObject = JSONObject.fromObject(content);   //此处可能出现Json数据格式不正确的情况
            JSONObject array = null;
            if (jsonObject.containsKey("comments")) {     //comments是一个JSONObject对象
                array = jsonObject.getJSONObject("comments");
            }
            Iterator iterator = array.keys();           //获得array中对应的键值
            commentJsons.clear();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                JSONObject value = array.getJSONObject(key);   //每个评论是一个JSONObject对象
                String user_comment="",user_id="",user_gender="",user_location="", user_createTime="",user_ip="",source="",commentId="";
                int against,favCount;
                if(value.containsKey("content")){
                    user_comment =reformat(value.getString("content"));
                }
                if(value.containsKey("ip"))
                {
                    user_ip=value.getString("ip");
                }
                if(value.containsKey("createTime")){
                    user_createTime =value.getString("createTime");
                }

                if(value.containsKey("commentId")){
                    commentId =value.getString("commentId");
                }

                if(value.containsKey("source")){
                    source =value.getString("source");
                }

                if(value.containsKey("favCount")){
                    favCount = Integer.parseInt(value.getString("favCount"));
                }

                if(value.containsKey("against")){
                    against = Integer.parseInt(value.getString("against"));
                }

                if(value.containsKey("user"))  //user是一个JSONObject对象
                {
                    JSONObject user= value.getJSONObject("user");
                    if (user.containsKey("location")) {
                        user_location=reformat(user.getString("location"));
                    }
                    if(user.containsKey("userId"))
                    {
                        user_id=user.getString("userId");
                    }
                }
                value.accumulate("pageId", newsPage.getPageId());
                value.accumulate("pageUrl",newsPage.getUrl());
                commentJsons.add(value.toString());
                Comment cmt = new Comment(pageId,"",user_gender,user_location,user_comment);
                cmt.setIp(user_ip);
                cmt.setTime(user_createTime);
                comments.add(cmt);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        setCommentJsons(commentJsons);
        return comments;
    }

    @Override
    public String tryCommentGet(String url, String page) {
        String _url="http://comment.edu.163.com/api/v1/products/a2869674571f77b5a0867c3d71db5856/threads/"+url+"/comments/newList";
        String content = crawlPage(_url, "utf-8");
        return content;
    }


    public static void main(String[] args) throws IOException, XMLStreamException {
        Configuration conf = new Configuration("crawlerSystem/conf/webcollector-wangyi.xml");
        WangyiParseComments wangyi = new WangyiParseComments(conf);
        NewsPage newsPage = new NewsPage();
        newsPage.setUrl("http://edu.163.com/15/0617/08/ASA4GRV800294MA8.html");
        ArrayList<Comment> comments=wangyi.getComments(newsPage);
        for(Comment comment:comments)
        {
            System.out.println(comment.getProvince()+"\t"+comment.getTime()+"\t"+comment.getGender()+"\t"+comment.getContent());
        }
    }

}
