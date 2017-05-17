package provider;


import lucene.Index;
import lucene.QueryResult;
import model.NewsPage;
import org.apache.lucene.document.Document;
import utils.SmartChineseSegmenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wlcheng on 1/4/16.
 */
public class LuceneSearch {
    public static void main(String[] args) {
        List<NewsPage> newsPageList = LuceneSearch.searchNewsPages("安徽", "./data-provider/Indexes/GzEdu/luceneIndex");
//        List<NewsPage> newsPageList = LuceneSearch.searchAllNewsPages("安徽", "./crawlerSystem/Indexes/");
        for (int i = 0; i < newsPageList.size(); i++) {
            System.out.println(newsPageList.get(i).toString());
        }
    }

    //static Index index = Index.getInstance("./crawlerSystem/Indexes/luceneIndex");
    static int maxDocument = 100;
    public static String query = "";


    public static List<NewsPage> searchAllNewsPages(String queryString,String indexPath){
        List<NewsPage> result = new ArrayList<NewsPage>();
        File dir = new File(indexPath);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            result.addAll(searchNewsPages(queryString,files[i].getAbsolutePath()+File.separator+"luceneIndex"));
        }
        return result;
    }

    public static List<NewsPage> searchNewsPages(String queryString,String indexPath) {
        Index index = Index.getInstance(indexPath); //"./crawlerSystem/Indexes/GxEdu/luceneIndex");
        query = queryString;
        List<NewsPage> newsPageList = new ArrayList<NewsPage>();
        QueryResult result = index.search(SmartChineseSegmenter.smartChineseAnalyzerSegment(queryString), 0, maxDocument);
        Map<Integer, Document> list = result.getRecordList();
        Set<Map.Entry<Integer, Document>> entrySet = list.entrySet();
        for (Map.Entry<Integer, Document> entry : entrySet) {
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
