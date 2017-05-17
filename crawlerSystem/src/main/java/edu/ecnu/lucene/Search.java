package edu.ecnu.lucene;

import cn.edu.hfut.dmic.contentextractor.NewsPage;
import edu.ecnu.util.SmartChineseSegmenter;
import org.apache.lucene.document.Document;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author wlcheng
 */
public class Search {


    public static void main(String[] args) {
        List<NewsPage> newsPageList = Search.searchNewsPages("2015");
        for (int i = 0; i < newsPageList.size(); i++) {
            System.out.println(newsPageList.get(i).toString());
        }
    }

    static Index index = Index.getInstance("./luceneIndex");

//    static Index index = Index.getInstance("./crawlerSystem/Indexes/luceneIndex");
    static int maxDocument = 100;
    public static String query = "";

    public static List<NewsPage> searchNewsPages(String queryString) {
        query = queryString;
        List<NewsPage> newsPageList = new ArrayList<NewsPage>();
        QueryResult result = index.search(SmartChineseSegmenter.smartChineseAnalyzerSegment(queryString), 0, maxDocument);
        Map<Integer, Document> list = result.getRecordList();
        Set<Entry<Integer, Document>> entrySet = list.entrySet();
        for (Entry<Integer, Document> entry : entrySet) {
            Document doc = entry.getValue();
            String title = doc.get("title");
            String url = doc.get("url");
            String time = doc.get("time");
            String content = doc.get("content");
            String publisher = doc.get("publisher");
            newsPageList.add(new NewsPage(url, title, content, time, publisher));
        }
        return newsPageList;
    }

}
