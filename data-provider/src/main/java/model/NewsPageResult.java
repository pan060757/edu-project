package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by binbin on 16/1/20.
 */
public class NewsPageResult {

    private Long totalCount;
    private int returnCount;
    private Long remainder;
    private List<NewsPage> newsPageList;

    public NewsPageResult() {
        newsPageList = new ArrayList<NewsPage>();
    }

    public NewsPageResult(Long totalCount, Long remainder, List newsPageList) {
        this.totalCount = totalCount;
        this.remainder = remainder;
        this.newsPageList = newsPageList;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getRemainder() {
        return remainder;
    }

    public void setRemainder(Long remainder) {
        this.remainder = remainder;
    }

    public int getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public List<NewsPage> getNewsPageList() {
        return newsPageList;
    }

    public void setNewsPageList(List<NewsPage> newsPageList) {
        this.newsPageList = newsPageList;
    }
}
