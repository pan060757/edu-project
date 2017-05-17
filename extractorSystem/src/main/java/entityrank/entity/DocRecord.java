package entityrank.entity;

import java.util.ArrayList;
import java.util.List;

public class DocRecord {

    private String newsId;
    private String title;
    private List<String> entityList;
    private String content;

    public DocRecord() {
        entityList = new ArrayList<String>();
    }

    public DocRecord(String newsId, String title, ArrayList<String> entityList, String content) {
        this.newsId = newsId;
        this.title = title;
        this.content = content;
        this.entityList = entityList;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<String> entityList) {
        this.entityList = entityList;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
