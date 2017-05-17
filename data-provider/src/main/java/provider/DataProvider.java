package provider;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import lucene.DocumentFactory;
import lucene.Index;
import model.Comment;
import model.NewsPage;
import mongodb.MongoDbManager;
import org.apache.lucene.document.Document;
import utils.Config;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by song on 2015/12/25.
 */

/**
 * 返回各模块最新的数据
 */
public class DataProvider extends Provider {
    static MongoDbManager mongoDbManager = MongoDbManager.getInstance();
    static String collectionName = Config.NEWS_PAGES_COLLECTION_NAME;
    protected static Index indexDAO = Index.getInstance();
    public static List<NewsPage> getLastNewsPages(String type) {
        if (type.contains("entity"))         //实体
        {
            return getLastNewsPagesBytype(type);
        }
        if (type.contains("relation_extraction"))         //关系抽取
        {
            return getLastNewsPagesBytype(type);
        } else if (type.contains("keysentence"))         //关键句
        {
            return getLastNewsPagesBytype(type);
        } else if (type.contains("classification"))       //分类
        {
            return getLastNewsPagesBytype(type);
        } else if (type.contains("deduplication"))       //去重
        {
            return getLastNewsPagesBytype(type);
        } else if (type.contains("event"))       //事件
        {
            return getLastNewsPagesBytype(type);
        } else
            return null;
    }

    /**
     * 根据类型，返回各模块最新的数据
     *
     * @param type
     * @return
     */
    public static List<NewsPage> getLastNewsPagesBytype(String type) {
        ArrayList<String> collectionList = mongoDbManager.getAllCollectionName();
        DBObject query = new BasicDBObject();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        query.put(type, 0);
        for (String collectionName : collectionList) {
            List<DBObject> relation = mongoDbManager.selectDocuments(collectionName, query);
            for (int i = 0; i < relation.size(); i++) {
                DBObject search_query = relation.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                // System.out.println(newspage);
                pageList.add(newspage);
            }
        }
        return pageList;
    }

    /**
     * 更新各模块已经访问过的页面的标志位（通过pageId)
     * @param type
     * @return
     */
    public static boolean updateNewsPages(String type, List<String> newspageIdList) {
        if (type.contains("entity"))         //实体
        {
            return updateNewsPagesByType(type, newspageIdList);
        }
        if (type.contains("relation_extraction"))         //关系抽取
        {
            return updateNewsPagesByType(type, newspageIdList);
        } else if (type.contains("keysentence"))         //关键句
        {
            return updateNewsPagesByType(type, newspageIdList);
        } else if (type.contains("classification"))       //分类
        {
            return updateNewsPagesByType(type, newspageIdList);
        } else if (type.contains("deduplication"))       //去重
        {
            return updateNewsPagesByType(type, newspageIdList);
        } else if (type.contains("event"))       //事件
        {
            return updateNewsPagesByType(type, newspageIdList);
        } else
            return false;
    }


    /**
     * 根据模块类型更新每个页面的访问状态
     *
     * @param type
     * @param newspageIdList
     * @return
     */
    public static boolean updateNewsPagesByType(String type, List<String> newspageIdList) {
        ArrayList<String> collectionList = mongoDbManager.getAllCollectionName();
        for (String collectionName : collectionList) {
            DBCollection collection = mongoDbManager.getDBCollection(collectionName);
            for (String newspageId : newspageIdList) {
                DBObject relation_query = new BasicDBObject();
                relation_query.put("pageId",newspageId);
                DBCursor cursor = collection.find();
                while (cursor.hasNext()) {
                    DBObject updateDocument = cursor.next();
                    updateDocument.put(type,1);
                    collection.update(relation_query, updateDocument);
                }
            }
        }
        return true;
    }



    public static HashMap<String,List<NewsPage>>getLastNewsPages2(String type) {
        if (type.contains("entity"))         //实体
        {
            return getLastNewsPagesBytype2(type);
        }
        if (type.contains("relation_extraction"))         //关系抽取
        {
            return getLastNewsPagesBytype2(type);
        } else if (type.contains("keysentence"))         //关键句
        {
            return getLastNewsPagesBytype2(type);
        } else if (type.contains("classification"))       //分类
        {
            return getLastNewsPagesBytype2(type);
        } else if (type.contains("deduplication"))       //去重
        {
            return getLastNewsPagesBytype2(type);
        } else if (type.contains("event"))       //事件
        {
            return getLastNewsPagesBytype2(type);
        } else
            return null;
    }


    /**
     * 根据类型，返回各模块最新的数据(包括数据源名，以及对应的新闻数据)
     * @param type
     * @return
     */
    public static HashMap<String,List<NewsPage>> getLastNewsPagesBytype2(String type) {
        ArrayList<String> collectionList = mongoDbManager.getAllCollectionName();
        HashMap<String,List<NewsPage>> pagemap= new HashMap<String,List<NewsPage>>();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        DBObject query = new BasicDBObject();
        query.put(type, 0);    //查询type字段为0的数据
        for (String collectionName : collectionList) {
            List<DBObject> relation = mongoDbManager.selectDocuments(collectionName, query);
            for (int i = 0; i < relation.size(); i++) {
                DBObject search_query = relation.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                // System.out.println(newspage);
                pageList.add(newspage);
            }
            String platform=pageList.get(0).getPublisher();    //得出此时的数据源名称
            pagemap.put(platform,pageList);
        }
        return pagemap;
    }


    /**
     * 更新各模块已经访问过的页面的标志位（通过pageId)
     * @param type
     * @return
     */
    public static boolean updateNewsPages2(String type,String platform,List<String> newspageIdList) {
        if (type.contains("entity"))         //实体
        {
            return updateNewsPagesByType2(type, platform, newspageIdList);
        }
        if (type.contains("relation_extraction"))         //关系抽取
        {
            return updateNewsPagesByType2(type, platform, newspageIdList);
        } else if (type.contains("keysentence"))         //关键句
        {
            return updateNewsPagesByType2(type,platform,newspageIdList);
        } else if (type.contains("classification"))       //分类
        {
            return updateNewsPagesByType2(type,platform,newspageIdList);
        } else if (type.contains("deduplication"))       //去重
        {
            return updateNewsPagesByType2(type,platform,newspageIdList);
        } else if (type.contains("event"))       //事件
        {
            return updateNewsPagesByType2(type,platform,newspageIdList);
        } else
            return false;
    }


    /**
     * 根据模块类型更新每个数据源已经访问过的每个页面的当前状态
     *
     * @param type
     * @param newspageIdList
     * @return
     */
    public static boolean updateNewsPagesByType2(String type,String platform,List<String> newspageIdList) {
        HashMap<String,String> map=mongoDbManager.getSourceNameToCollectionName();
        String collectionName=map.get(platform);
        DBCollection collection = mongoDbManager.getDBCollection(collectionName);
        for (String newspageId : newspageIdList) {
            DBObject relation_query = new BasicDBObject();
            relation_query.put("pageId",newspageId);
            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                DBObject updateDocument = cursor.next();
                updateDocument.put(type,0);
                collection.update(relation_query, updateDocument);
            }
        }
        return true;
    }
    /**
     * 获得指定时间段内的新闻
     *
     * @param from
     * @param to
     * @return
     */
    public static List<NewsPage> getNewsPagesByDate1(Date from, Date to) {
        ArrayList<String> collectionList = mongoDbManager.getAllCollectionName();
        String datefrom = new SimpleDateFormat("yyyy-MM-dd").format(from);
        String dateto = new SimpleDateFormat("yyyy-MM-dd").format(to);
        DBObject relation_query = new BasicDBObject();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        for (String collectionName : collectionList) {
            List<DBObject> docs = mongoDbManager.selectDocuments(collectionName, relation_query);
            for (int i = 0; i < docs.size(); i++) {
                DBObject search_query = docs.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                String date = newspage.getTime().substring(0, 10);
                if (date.compareTo(datefrom) > 0 && date.compareTo(dateto) < 0) {
                    pageList.add(newspage);
                }
            }
        }
        return pageList;
    }

    /**
     * 根据网站类型返回相应的数据
     *
     * @param platform
     * @return
     */
    public static List<NewsPage> getNewsPagesByPlatform(String platform) {
        HashMap<String,String> map=mongoDbManager.getSourceNameToCollectionName();
        String collectionName=map.get(platform);
        DBObject relation_query = new BasicDBObject();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        List<DBObject> docs = mongoDbManager.selectDocuments(collectionName, relation_query);
        for (int i = 0; i < docs.size(); i++) {
            DBObject search_query = docs.get(i);
            Gson gson = new Gson();
            NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
            // System.out.println(newspage);
            pageList.add(newspage);
        }
        return pageList;
    }

    /****
     * 根据pageId返回相应的评论
     * @param newspageIdList
     * @return
     */
    public static HashMap<String, List<Comment>> getCommentsByPageId1(List<String> newspageIdList) {
        HashMap<String, List<Comment>> map = new HashMap<String, List<Comment>>();
        for (String pageId :newspageIdList) {
            DBObject query=new BasicDBObject();
            query.put("pageId",pageId);
            List<DBObject> docs = mongoDbManager.selectDocuments(collectionName,query);
            for (int i = 0; i < docs.size(); i++) {
                DBObject search_query = docs.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                map.put(newspage.getPageId(),newspage.getComments());
            }
        }
        return map;
    }

    /****
     * 根据给定的页面pageId，返回对应的评论列表
     * @param pageId
     * @return
     */
    public static List<Comment> getCommentsByNewsPageId1(String pageId) {
        List<Comment> comment=new ArrayList<Comment>();
        DBObject query=new BasicDBObject();
        query.put("pageId",pageId);
        List<DBObject> docs = mongoDbManager.selectDocuments(collectionName, query);
        for (int i = 0; i < docs.size(); i++) {
            DBObject search_query = docs.get(i);
            Gson gson = new Gson();
            NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
            comment=newspage.getComments();
        }
        return comment;
    }
    /****
     * 返回当前数据库内的所有新闻
     */
    public static List<NewsPage> getAllNewsPages() {
        ArrayList<String> collectionList = mongoDbManager.getAllCollectionName();
        DBObject query = new BasicDBObject();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        for (String collection : collectionList) {
            List<DBObject> relation = mongoDbManager.selectDocuments(collection, query);
            for (int i = 0; i < relation.size(); i++) {
                DBObject search_query = relation.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                pageList.add(newspage);
            }
        }
        return pageList;
    }

    /****
     * 根据数据库表名获得当前表中所有记录
     * @return
     */
    public static List<NewsPage> getAllNewsPages(String collectionName) {
        DBObject query = new BasicDBObject();
        List<NewsPage> pageList = new ArrayList<NewsPage>();
        List<DBObject> relation = mongoDbManager.selectDocuments(collectionName, query);
        for (int i = 0; i < relation.size(); i++) {
                DBObject search_query = relation.get(i);
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                pageList.add(newspage);
        }
        return pageList;
    }
    public static void main(String args[]) {

        //测试返回各模块最新的数据
//        List<NewsPage> relationList=getLastNewsPages("entity");
//        for(NewsPage page:relationList)
//        {
//            System.out.println(page);
//        }

        //根据时间段返回对应的新闻列表
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
//        String dstr = "2015-01-01";
//        String dstr1 = "2015-12-31";
//        try {
//            Date date1 = sdf.parse(dstr.toString());
//            Date date2 = sdf.parse(dstr1.toString());
//            List<NewsPage> pagelist = getNewsPagesByDat(date1, date2);
//            for(NewsPage page:pagelist)
//        {
//            System.out.println(page);
//        }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//


        //更新各模块已经访问过的新闻的相应标志位(为了提高访问速度，需要指定相应的平台)
//        List<String> pageList=new ArrayList<String>();
//        pageList.add("1451372852670");
//        pageList.add("1451372852810");
//        pageList.add("1451372852810");
//        updateNewsPagesByType2("entity","北京教育网",pageList);



        //根据网站类型返回相应的数据
//        List<NewsPage> pageList=getNewsPagesByPlatform("北京教育网");
//        for(NewsPage page:pageList)
//        {
//            System.out.println(page);
//        }




        //
//            Iterator iter=mongoDbManager.dbCollectionMap.entrySet().iterator();
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                String collectionName = (String) entry.getKey();
//                DBCollection collection = (DBCollection)entry.getValue();
//                System.out.println(collectionName);
//            }
//            ArrayList<String>collectionList=mongoDbManager.getAllCollectionName();
//            for(String collection:collectionList)
//            {
//                System.out.println(collection);
//            }



        //测试根据pageIdList获得相应页面的儿评论内容,测试数据用的是腾讯教育的数据
//        List<String> pageList=new ArrayList<String>();
//        pageList.add("1451526455309");
//        pageList.add("1451526499499");
//        pageList.add("1451526520733");
//        HashMap<String, List<Comment>> map=getCommentsByPageId1(pageList);
//        Iterator iter=map.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            Map.Entry entry=(Map.Entry)iter.next();
//            String pageId=(String)entry.getKey();
//            List<Comment> commentList=(List<Comment>)entry.getValue();
//            for(Comment comment:commentList)
//            {
//                System.out.println(pageId+"\t"+comment.getContent()+"\t"+comment.getTime());
//            }
//        }



        //测试根据单个pageId获得相应的评论内容，测试时用的是腾讯教育的数据
//        List<Comment> commentList=getCommentsByNewsPage1("1451526455309");
//        for(Comment comment:commentList)
//        {
//            System.out.println(comment.getContent()+"\t"+comment.getTime());
//        }



        //获得当前数据库内所有的新闻页面
//            List<NewsPage> pagelist = getAllNewsPages();
//            for (NewsPage page : pagelist) {
//                System.out.println(page);
//            }



        //测试数据库表名与数据源之间的对应关系(在getNewsPagesByPlatform中可能需要使用两者的对应关系)
//        HashMap<String,String> map=mongoDbManager.getSourceNameToCollectionName();
//        Iterator iter=map.entrySet().iterator();
//        while(iter.hasNext())
//        {
//            Map.Entry entry=(Map.Entry)iter.next();
//            String publisher=(String)entry.getKey();
//            String tableName=(String)entry.getValue();
//            System.out.println(publisher+"\t"+tableName);
//        }

        //为数据库中已有的数据添加索引
        List<NewsPage>pageList=getAllNewsPages("newspages1");
        for(NewsPage newspage:pageList)
        {
            Document document = DocumentFactory.getDocumentByNewsPage(newspage);
            if (document!=null){
                System.out.println("开始建立索引");
                indexDAO.add(document);          //给数据库记录数据添加索引
            }
        }
    }
}
