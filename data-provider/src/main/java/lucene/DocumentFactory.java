package lucene;


import model.NewsPage;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * Created by wlcheng on 12/18/15.
 */
public class DocumentFactory {


    public static Document getDocumentByNewsPage(NewsPage newsPage){
        if(null==newsPage) return null;
        Document doc = new Document();
        doc.add(new TextField("title", newsPage.getTitle(), Field.Store.YES));
        doc.add(new StringField("time", newsPage.getTime()==null? "":newsPage.getTime(), Field.Store.YES));
        doc.add(new StringField("url", newsPage.getUrl(), Field.Store.YES));
        doc.add(new StringField("publisher", newsPage.getPublisher()==null? "":newsPage.getPublisher(), Field.Store.YES));
        doc.add(new TextField("content", newsPage.getContent(), Field.Store.YES));
        return doc;
    }
}
