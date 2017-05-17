package edu.ecnu.storage;

import cn.edu.hfut.dmic.contentextractor.Comment;
import cn.edu.hfut.dmic.contentextractor.NewsPage;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import edu.ecnu.lucene.DocumentFactory;
import edu.ecnu.lucene.Index;
import edu.ecnu.util.Config;
import edu.ecnu.util.FileUtils;
import edu.ecnu.util.StringUtils;
import edu.ecnu.socket.*;
import org.apache.lucene.document.Document;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by wlcheng on 12/18/15.
 */
public class InfoRecoder {


    protected static String defaultPath="."+ File.separator+"default";
    protected static String sourceFilePath = defaultPath+File.separator+"news_source";
    protected static String newsPageFilePath =defaultPath+File.separator+"newspages";
    protected static String commentsFilePath =defaultPath+File.separator+"comments";
    protected static String commentSourceFilePath =defaultPath+File.separator+"comments_source";

    protected static File root = new File(defaultPath);
    protected static  File sourceDir = new File(sourceFilePath);
    protected static File newsPageDir = new File(newsPageFilePath);
    protected static  File commentsDir = new File(commentsFilePath);
    protected static  File commentSourceDir = new File(commentSourceFilePath);


    protected static MongoDbManager mongoDbManager = MongoDbManager.getInstance();
    protected static Index indexDAO = Index.getInstance();
    private static int BUFFER_SIZE = 5;
    private static NewsPage[]  buffer = new NewsPage[BUFFER_SIZE];
    private static int pointer = 0;


    public static void main(String[] args) {
        check();
        String url = "http://www.ahedu.gov.cn/21/view/287211.shtml.sourcefile";
        System.out.println(url.replaceAll("/","-"));
    }

    public synchronized static void save(NewsPage newsPage){
        check();
//        System.out.println(StringUtils.Jsonformat(newsPage));
        if(pointer < BUFFER_SIZE) {
            if (newsPage != null) {
                buffer[pointer++] = newsPage;
            }
        }

        if(pointer==BUFFER_SIZE){
          //  saveIndexInBatch();
            saveHtmlInBatch();
            saveNewsPageInBatch();
            pointer = 0;
        }

    }

    public synchronized static void flush(){
        check();
        if(pointer!=BUFFER_SIZE){
            // saveIndexInBatch();
            saveHtmlInBatch();
            saveNewsPageInBatch();
            pointer = 0;
        }
    }

    public synchronized static void saveComments(List<Comment> comments){
        if(comments==null) return;
        check();

        //写源文件
        for (int i = 0; i < comments.size(); i++) {
            FileUtils.write(commentsFilePath + File.separator + "comments.txt", comments.get(i) + "\r\n");
        }
    }

    public synchronized static void saveCommentSource(List<String> comment_sources){
        if(comment_sources==null) return;
        check();
        for (int i = 0; i < comment_sources.size(); i++) {
            FileUtils.write(commentSourceDir + File.separator + "comment.txt", comment_sources.get(i).toString() + "\r\n");
        }
    }

    private synchronized static void saveIndexInBatch(){
        boolean opt = false;
        for (int i = 0; i < pointer; i++) {
            Document document = DocumentFactory.getDocumentByNewsPage(buffer[i]);
            if (document!=null){
                indexDAO.add(document);
                opt = true;
            }
        }
        if(opt) indexDAO.optimize();
    }

    private synchronized static void saveHtmlInBatch(){
        for (int i = 0; i < pointer; i++) {
            NewsPage newsPage = buffer[i];
            String filePath = sourceFilePath + File.separator +newsPage.getPublisher()+"-"+System.currentTimeMillis()+".sourcefile";
            FileUtils.write(filePath, newsPage.getHtml());
        }
    }

    private synchronized static void saveNewsPageInBatch(){
        for (int i = 0; i < pointer; i++) {
            NewsPage newsPage = buffer[i];

            // 存储源文件到本地磁盘
            String newsPageJson = StringUtils.Jsonformat(newsPage);
            FileUtils.write(newsPageFilePath+File.separator+"newspages.json", newsPageJson+"\r\n");

            // 存储源数据到MongoDB
            DBObject document =(DBObject) JSON.parse(newsPageJson);
            for (int j = 0; j < Config.attachedKeys.length; j++) {
                document.put(Config.attachedKeys[j],0);
            }

            mongoDbManager.addIndexes(Config.NEWS_PAGES_COLLECTION_NAME+ newsPage.getSourceId(),1,Config.indexsKeys);
            mongoDbManager.insertDocument(Config.NEWS_PAGES_COLLECTION_NAME + newsPage.getSourceId(), document);


            // 存储评论数据到本地磁盘和MongoDB

            StringBuffer stringBuffer = new StringBuffer();
            List<String> cmt_source = newsPage.getComments_source();
            if(cmt_source!=null){
                for (int j = 0; j < newsPage.getComments_source().size(); j++) {
                    String comments = newsPage.getComments_source().get(j);
                    DBObject comment =(DBObject) JSON.parse(comments);
                    comment.put("pageId",newsPage.getPageId());
                    mongoDbManager.insertDocument(Config.COMMENTS_COLLECTION_NAME+newsPage.getSourceId(),comment);
                    stringBuffer.append(comment.toString()).append("\r\n");
                }
                FileUtils.write(commentSourceFilePath + File.separator + "comments_source.json", stringBuffer.toString());
            }
        }
    }


    private synchronized static void check(){

        if(!root.exists()){
            root.mkdir();
        }

        if(!sourceDir.exists()){
            sourceDir.mkdir();
        }

        if(!newsPageDir.exists()){
            newsPageDir.mkdir();
        }


        if(!commentsDir.exists()){
            commentsDir.mkdir();
        }
        if(!commentSourceDir.exists()){
            commentSourceDir.mkdir();
        }

    }

}
