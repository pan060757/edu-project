package edu.ecnu.spcrawler.Tencent;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduParseComment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by song on 2015/12/16.
 */
public class TencentParseComments extends BaseEduParseComment {

    public ArrayList<Comment> getComments(NewsPage newsPage){
        String url = newsPage.getUrl();
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String cmtID = getCmtID(url);
        String content = tryCommentGet(cmtID);
        content = content.replace("mainComment(","");
        content = content.substring(0,content.length()-1);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("data");

        JSONArray array;
        if(jsonObject!=null && jsonObject.containsKey("commentid")){
            array=jsonObject.getJSONArray("commentid");
        }
        else{
            return comments;
        }
        Iterator<JSONObject> iter = array.iterator();
        commentJsons.clear();
        while(iter.hasNext()){
            JSONObject comment = iter.next();
            String text="",user_id="",user_gender="",user_region="",time="";
            if(comment.containsKey("content")){
                text =reformat(comment.getString("content"));
            }
            if(comment.containsKey("time")){
                time = parseUnixTime(comment.getString("time"));
            }
            if(comment.containsKey("userinfo")){
                JSONObject userinfo = comment.getJSONObject("userinfo");
                if(userinfo.containsKey("gender")){
                    user_gender = userinfo.getString("gender");
                }
                if(userinfo.containsKey("region")){
                    user_region=userinfo.getString("region");
                }
                if(userinfo.containsKey("userid")){
                    user_id=userinfo.getString("userid");
                }
            }
            comment.accumulate("pageId", newsPage.getPageId());
            comment.accumulate("pageUrl", newsPage.getUrl());
            commentJsons.add(comment.toString());
            Comment cmt = new Comment(newsPage.getPageId(),user_id,null,user_region,text);
            cmt.setTime(time);
            comments.add(cmt);
        }
        setCommentJsons(commentJsons);
        return comments;
    }

    public String tryCommentGet(String cmt_id){
        String url="http://coral.qq.com/article/"+cmt_id+"/comment?commentid=0&reqnum=1000&tag=&callback=mainComment";
        String content =crawlPage(url);
        return content;
    }

    public String getCmtID(String url){
        String content = crawlPage(url);
        Pattern p = Pattern.compile("(^|&|\\\\?)cmt_id = (\\d{10})(;)");
        Matcher matcher = p.matcher(content);
        if(matcher.find() && matcher.groupCount()>=1){
            String match = matcher.group(0);
            match = match.replace("cmt_id = ","");
            match = match.replace(";","");
            return match;
        }
        else return "";
    }
    @Override
    public String tryCommentGet(String url, String page) {
        return null;
    }
}
