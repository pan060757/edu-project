package edu.ecnu.crawler.Ifeng;

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
public class IfengParseComments extends BaseEduParseComment {

    public IfengParseComments(Configuration conf){
        this.conf = conf;
    }

    @Override
    public ArrayList<Comment> getComments(NewsPage newsPage){
        String url = newsPage.getUrl();
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        int page=0;
        while (true){
            page++;
            String content = tryCommentGet(url,String.valueOf(page));
            if(content.length()<100){
                break;
            }

            JSONObject jsonObject = JSONObject.fromObject(content);
            JSONArray array=null;
            if(jsonObject.containsKey("comments")){
                array = jsonObject.getJSONArray("comments");
            }

            if(array!=null){
                Iterator<JSONObject> iter = array.iterator();
                commentJsons.clear();
                while (iter.hasNext()){
                    try {
                        JSONObject obj = iter.next();

                        String text = "", comment_id="",doc_url="",doc_name="",user_id = "", user_gender = "0", user_region = "", time = "", user_name="",user_ip = "", user_type = "";
                        if (obj.containsKey("client_ip")) {
                            user_ip = obj.getString("client_ip");
                        }

                        if (obj.containsKey("ip_from")) {
                            user_region = obj.getString("ip_from");
                        }
                        if (obj.containsKey("comment_contents")) {
                            text = reformat(obj.getString("comment_contents"));
                        }
                        if (obj.containsKey("comment_date")) {
                            time = parseUnixTime(obj.getString("comment_date"));
                        }
                        if (obj.containsKey("useragent")) {
                            user_type = obj.getString("useragent");
                        }

                        if (obj.containsKey("uname")) {
                            user_name = obj.getString("uname");
                        }

                        if (obj.containsKey("comment_id")) {
                            comment_id = obj.getString("comment_id");
                        }

                        if (obj.containsKey("user_id")) {
                            user_id = reformat(obj.getString("user_id"));
                        }

                        if (obj.containsKey("doc_url")) {
                            doc_url = obj.getString("doc_url");
                        }

                        if (obj.containsKey("doc_url")) {
                            doc_name = obj.getString("doc_name");
                        }
                        obj.accumulate("pageId", newsPage.getPageId());
                        obj.accumulate("pageUrl", newsPage.getUrl());
                        commentJsons.add(obj.toString());
                        Comment cmt = new Comment(newsPage.getPageId(),user_name,user_gender,user_region,text);
                        cmt.setUserType(user_type);
                        cmt.setTime(time);
                        cmt.setIp(user_ip);
                        comments.add(cmt);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                setCommentJsons(commentJsons);
            }
        }
        return comments;
    }


    public ArrayList<Comment> getComments(String url) {
        return null;
    }

    @Override
    public String tryCommentGet(String url,String page){
        url = url.replace("/","%2F").replace(":","%3A").trim();
        url="http://comment.ifeng.com/get.php?docurl="+url+
                "&format=json&job=1&pagesize=100&p="+page+"&callback=newCommentCallback";
        String content =crawlPage(url);
        return content;
    }

    //测试用例
    public static void main(String args[])
    {
        String url="http://edu.ifeng.com/a/20150818/41420590_0.shtml";
        NewsPage newsPage = new NewsPage();
        newsPage.setUrl(url);
        newsPage.setPageId("123");
        ArrayList<Comment> comments=new IfengParseComments( new Configuration("crawlerSystem/conf/webcollector-ifeng.xml")).getComments(newsPage);
        for(Comment comment:comments)
        {
            System.out.println("\t"+comment.getTime()+"\t"+"\t"+comment.getGender()+"\t"+"\t");
        }
    }
}
