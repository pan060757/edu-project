package lucene;

import model.NewsPage;
import org.apache.lucene.document.Document;
import utils.SmartChineseSegmenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wlcheng
 */
public class Search {


    public static void main(String[] args) {
        List<NewsPage> newsPageList = Search.searchNewsPages("安徽");
        for (int i = 0; i < newsPageList.size(); i++) {
            System.out.println(newsPageList.get(i).toString());
        }
    }


    static Index index = Index.getInstance("./luceneIndex");
    static int maxDocument = 2000;
    public static String query = "";

    public static List<NewsPage> searchNewsPages(String queryString) {
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
