/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.contentextractor;

import net.sf.json.JSONString;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hu
 */
public class NewsPage{

    protected String url = null;
    protected String title = null;
    protected String content = null;

    public String getContent() {
        return content;
    }

    protected String time = null;
    protected String publisher = null;

    @JsonIgnore  //表示将该对象转换为json String的时候，忽略该属性
    protected String html = null;

    protected String author = null;
    protected String pageId = null;
    protected String sourceId = null;
    
    protected List<Comment> comments = null;

    @JsonIgnore
    protected List<String> comments_source = null;

    public List<String> getComments_source() {
        return comments_source;
    }

    public void setComments_source(List<String> comments_source) {
        this.comments_source = comments_source;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public NewsPage(String url, String title, String content, String time, String publisher, String author, String pageId, String sourceId) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.time = time;
        this.publisher = publisher;
        this.author = author;
        this.pageId = pageId;
        this.sourceId = sourceId;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getContent() {
//        if (content == null) {
//            if (contentElement != null) {
//                content = contentElement.text();
//            }
//        }
//        return content;
//    }

    public NewsPage() {
    }

    public NewsPage(String url, String title, String content, String time, String publisher) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.time = time;
        this.publisher = publisher;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

//    @Override
//    public String toString() {
//        return title + "\t" +url +"\t"+ publisher+"\t" + time + "\t" + getContent();
//    }


    @Override
    public String toString() {
        return "{" +
                "'url':" + "\""+url +"\""+
                ", 'title':" + "\""+title +"\""+
                ", 'content':" + "\""+content +"\""+
                ", 'time':" + "\""+time +"\""+
                ", 'publisher':" + "\""+publisher +"\""+
                ", 'author':" + "\""+author +"\""+
                ", 'pageId':" + "\""+pageId +"\""+
                ", 'sourceId':" + "\""+sourceId +"\""+
                ", 'comments':" + "\""+comments +"\""+
                '}';
    }

}
