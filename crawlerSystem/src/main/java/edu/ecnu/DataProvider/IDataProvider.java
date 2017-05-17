package edu.ecnu.DataProvider;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wlcheng on 12/21/15.
 */
public interface IDataProvider {


    public List<NewsPage> getLastNewsPages(String... type);


    public boolean updateNewsPages(String condition);


    public List<NewsPage> getNewsPagesBySearch(String keyword);


    public List<NewsPage> getNewsPagesBySearch(String keyword,String... platform);


    public List<NewsPage> getNewsPagesByDate(Date from, Date to);


    public List<NewsPage> getNewsPagesByPlatform(String platform,String... condition);

    public List<NewsPage> getNewsPagesByPulisher(String publisher,String... condition);

    public HashMap<String,List<Comment>> getCommentsByNewsPage(List<NewsPage> newsPage);

    public List<Comment> getCommentsByNewsPage(NewsPage newsPage);

}
