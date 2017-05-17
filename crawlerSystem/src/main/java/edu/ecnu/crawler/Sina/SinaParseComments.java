package edu.ecnu.crawler.Sina;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import cn.edu.hfut.dmic.webcollector.util.Configuration;
import edu.ecnu.crawler.BaseEduCrawler.BaseEduParseComment;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by song on 2015/12/16.
 */
public class SinaParseComments  extends BaseEduParseComment {

    public SinaParseComments(Configuration conf){
        super.conf = conf;
    }
    @Override
    public ArrayList<Comment> getComments(NewsPage newsPage){
        String url = newsPage.getUrl();
        System.out.println("Add comments for "+url);
        ArrayList<Comment> comments = new ArrayList<Comment>();
        String meta = getCmtMeta(url);
        if("".equals(meta)){
            return comments;
        }

        String cmtID = getCmtID(meta);
        String channel = getChannel(meta);

        String content = tryCommentGet(cmtID,channel);
        JSONObject jsonObject = JSONObject.fromObject(content);
        jsonObject = jsonObject.getJSONObject("result");

        JSONArray array=null;
        if(jsonObject.containsKey("cmntlist")){
            array = jsonObject.getJSONArray("cmntlist");
        }

        if(array!=null){
            Iterator<JSONObject> iter = array.iterator();
            commentJsons.clear();
            while (iter.hasNext()){
                JSONObject obj = iter.next();

                String text="",user_id="",user_gender="0",user_region="",time="",user_ip="",user_type="",user_name="",against="",agree="";

                if(obj.containsKey("ip")){
                    user_ip=obj.getString("ip");
                }
                if(obj.containsKey("area")) {
                    user_region = obj.getString("area");
                }
                if(obj.containsKey("content")) {
                    text=reformat(obj.getString("content"));
                }
                if(obj.containsKey("nick")){
                    user_id=reformat(obj.getString("nick"));
                }
                if(obj.containsKey("time")){
                    time = obj.getString("time");
                }
                if(obj.containsKey("usertype")){
                    user_type = obj.getString("usertype");
                }
                if(obj.containsKey("nick")){
                    user_name = obj.getString("nick");
                }

                if(obj.containsKey("ip")){
                    user_ip = obj.getString("ip");
                }

                if(obj.containsKey("against")){
                    against = obj.getString("against");
                }

                if(obj.containsKey("agree")){
                    agree = obj.getString("agree");
                }
                obj.accumulate("pageId", newsPage.getPageId());
                obj.accumulate("pageUrl",newsPage.getUrl());
                commentJsons.add(obj.toString());
                Comment cmt = new Comment(newsPage.getPageId(),user_name,null,user_region,text);
                cmt.setTime(time);
                cmt.setIp(user_ip);
                cmt.setUserType(user_type);
                comments.add(cmt);
            }
            setCommentJsons(commentJsons);
        }
        return comments;
    }


    public String tryCommentGet(String cmt_id,String channel){
        String url="http://comment5.news.sina.com.cn/page/info?version=1&format=json&channel="+channel+
                "&newsid="+cmt_id+"&group=0&compress=1&ie=gbk&oe=gbk&page=1&page_size=200";
        /***
         * http://comment5.news.sina.com.cn/page/info?format=js&channel=wj&newsid=352-1-453900&group=0&compress=1&ie=gbk&oe=gbk&page=1&page_size=20&jsvar=requestId_36874386
         */
        String content = crawlPage(url);
        return content;

    }
    public String getCmtMeta(String url){
        String suffix = conf.get("crawler.suffix");
        String content = crawlPage(url);
        Document doc = Jsoup.parse(content);
        String nextSelector = conf.get(suffix + ".comment.selector");
        Elements eles = doc.select(nextSelector);
        if(eles!=null && eles.size()>0){
            Element ele = eles.get(0);
            String chanID = ele.attr("content");
            String[] temp = chanID.split(":");
            if(temp.length>1){
                return chanID;
            }
        }
        return "";
    }

    public String getChannel(String meta){
        if(!"".equals(meta)){
            return meta.split(":")[0];
        }
        return "";
    }
    public String getCmtID(String meta){
        if(!"".equals(meta)){
            return meta.split(":")[1];
        }
        return "";
    }
    public static void main(String[] args) throws IOException, XMLStreamException {
        SinaParseComments sina = new SinaParseComments(new Configuration("crawlerSystem/conf/webcollector-sina.xml"));
        NewsPage newsPage = new NewsPage();
        newsPage.setUrl("http://edu.sina.com.cn/gaokao/2015-01-12/0759453900.shtml");
        ArrayList<Comment> comments=sina.getComments(newsPage);
        for(Comment comment:comments)
        {
            System.out.println(comment.getIp()+"\t"+comment.getTime()+"\t"+comment.getLocation()+"\t"+comment.getGender()+"\t"+comment.getTime()+"\t"+comment.getContent());
        }
    }
}
