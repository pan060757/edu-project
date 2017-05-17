package edu.ecnu.DataProvider;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wlcheng on 12/22/15.
 */
public class Provider implements IDataProvider {

    public List<NewsPage> getLastNewsPages(String... type) {
        return null;
    }

    public boolean updateNewsPages(String condition) {
        return false;
    }

    public List<NewsPage> getNewsPagesBySearch(String keyword) {
        return null;
    }

    public List<NewsPage> getNewsPagesBySearch(String keyword, String... platform) {
        return null;
    }

    public List<NewsPage> getNewsPagesByDate(Date from, Date to) {
        return null;
    }

    public List<NewsPage> getNewsPagesByPlatform(String platform, String... condition) {
        return null;
    }

    public List<NewsPage> getNewsPagesByPulisher(String publisher, String... condition) {
        return null;
    }

    public HashMap<String, List<Comment>> getCommentsByNewsPage(List<NewsPage> newsPage) {
        return null;
    }

    public List<Comment> getCommentsByNewsPage(NewsPage newsPage) {
        return null;
    }
}
