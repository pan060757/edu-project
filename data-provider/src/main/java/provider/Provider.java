package provider;
import model.Comment;
import model.NewsPage;

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
    public HashMap<String,List<Comment>> getCommentsByPageId(List<String> newsPageIdList){return null;}

    public List<Comment> getCommentsByNewsPageId(String pageId){return null;}
}
