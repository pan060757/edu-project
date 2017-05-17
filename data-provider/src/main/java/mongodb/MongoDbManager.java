package mongodb;

import com.google.gson.Gson;
import com.mongodb.*;
import model.NewsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wlcheng on 12/24/15.
 */
public class MongoDbManager {

    protected static Logger logger = LoggerFactory.getLogger(MongoDbManager.class);
    private static MongoDbManager instance;
    private static ResourceBundle rb = ResourceBundle.getBundle("db");
    private String DBNAME = null;
    private Mongo mongo = null;
    private DB dbConnection = null;
    public DB getDbConnection() {
        return dbConnection;
    }
    private String mongoServerAddr=null;;
    private int mongoServerPort;
    public static Map<String, DBCollection> dbCollectionMap = new ConcurrentHashMap<String, DBCollection>();

    public MongoDbManager() {
        // 配置默认服务器的IP地址和端口以及所使用的数据库
        mongoServerAddr=rb.getString("ServerAddrIP");
        mongoServerPort = Integer.valueOf(rb.getString("mongoServerPort"));
        DBNAME= rb.getString("DBNAME");
        init();
    }
    public synchronized  static  MongoDbManager getInstance(){
        if(instance==null){
            instance = new MongoDbManager();
        }
        return instance;
    }

    public void setMongoServerAddr(String mongoServerAddr) {
        this.mongoServerAddr = mongoServerAddr;
    }

    public void setMongoServerPort(int mongoServerPort) {
        this.mongoServerPort = mongoServerPort;
    }

    private void init() {
        if (this.mongo == null) {
            this.mongo = new Mongo( this.mongoServerAddr , this.mongoServerPort);
            if (null != this.mongo) {
                this.dbConnection = this.mongo.getDB(DBNAME);
                System.out.println("initialize the mongodb collection success!");
            }
        }
    }

    /**
     * get an table
     * @param collectionName
     * @return
     */
    public DBCollection getDBCollection(String collectionName) {
        DBCollection collection = null;
        if (dbCollectionMap.containsKey(collectionName)) {
            collection = dbCollectionMap.get(collectionName);
        } else {
            collection = this.dbConnection.getCollection(collectionName);
            if (null != collection) {
                dbCollectionMap.put(collectionName, collection);
            }
        }
        return collection;
    }

    /**
     * check if doc exsit
     * @param collectionName table name
     * @param query target document
     */
   
    public boolean isDocumentExsit(String collectionName, DBObject query) {
        boolean result = false;
        DBCursor dbCursor = null;
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            dbCursor = collection.find(query);
            if (null != dbCursor && dbCursor.hasNext()) {
                result = true;
            }
        }
        return result;
    }
    /**
     * query an record
     * @param collectionName table name
     * @param query target document
     * @return
     */
    public DBObject selectDocument(String collectionName, DBObject query) {
        DBObject result = null;
        DBCursor dbCursor = null;
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            dbCursor = collection.find(query);
            if (null != dbCursor && dbCursor.hasNext()) {
                result = dbCursor.next();
            }
        }
        return result;
    }


    /**
     * query an record
     * @param collectionName table name
     * @param query target document
     * @return
     */
    public List<DBObject> selectDocuments(String collectionName, DBObject query) {
        List<DBObject> result = new ArrayList<DBObject>();
        DBCursor dbCursor = null;
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            Iterator<DBObject> dbObjectIterator = collection.find(query).iterator();
            while(dbObjectIterator.hasNext()){
                result.add(dbObjectIterator.next());
            }
        }
        return result;
    }


    /**
     *     /**
     * insert an new record
     * @param collectionName table name
     * @param newDocument new doc
     */
    public void insertDocument(String collectionName, DBObject newDocument) {
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            if (!this.isDocumentExsit(collectionName, newDocument)) {//insert only doc not exist
                collection.insert(newDocument);
            }
        }
    }

    /**
     * update an document
     * @param collectionName
     * @param query target document
     * @param updatedDocument
     * @return
     */
    public boolean updateDocument(String collectionName, DBObject query, DBObject updatedDocument) {
        boolean result = false;
        WriteResult writeResult = null;
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            writeResult = collection.update(query, updatedDocument);
            if (null != writeResult) {
                if (writeResult.getN() > 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * delete an document
     * @param collectionName
     * @return
     */
    
    public boolean deleteDocument(String collectionName, DBObject query) {
        boolean result = false;
        WriteResult writeResult = null;
        DBCollection collection = this.getDBCollection(collectionName);
        if (null != collection) {
            writeResult = collection.remove(query);
            if (null != writeResult) {
                if (writeResult.getN() > 0) {
                    result = true;
                }
            }
        }
        return result;
    }

    /****
     * 返回各个数据源的新闻列表
     * @return
     */
    public ArrayList<String> getAllCollectionName()
    {
        ArrayList<String>collectionList=new ArrayList<String>();
        Mongo m=new Mongo(mongoServerAddr,mongoServerPort);
        //得到数据库
        DB db=m.getDB(DBNAME);
        for(String collection:db.getCollectionNames()){
            if(collection.contains("newspages"))
                collectionList.add(collection);
        }
        return collectionList;
    }


    /****
     * 返回各个数据源表名与数据源平台对应关系
     * @return
     */
    public HashMap<String,String> getSourceNameToCollectionName()
    {
        HashMap<String,String>map=new HashMap<String,String>();
        Mongo m=new Mongo(mongoServerAddr,mongoServerPort);
        //得到数据库
        DB db=m.getDB(DBNAME);
        for(String collectionName:db.getCollectionNames()){
            if(collectionName.contains("newspages"))
            {
                DBObject query = new BasicDBObject();
                List<DBObject> docs = instance.selectDocuments(collectionName,query);
                DBObject search_query = docs.get(0);  //取第一条数据即可
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                String publisher=newspage.getPublisher();
                map.put(publisher,collectionName);
            }
        }
        return map;
    }

    /****
     * 返回各个数据源表名与数据源平台对应关系
     * @return
     */
    public HashMap<String,String> getCollectionNameToSourceName()
    {
        HashMap<String,String>map=new HashMap<String,String>();
        Mongo m=new Mongo(mongoServerAddr,mongoServerPort);
        //得到数据库
        DB db=m.getDB(DBNAME);
        for(String collectionName:db.getCollectionNames()){
            if(collectionName.contains("newspages"))
            {
                DBObject query = new BasicDBObject();
                List<DBObject> docs = instance.selectDocuments(collectionName,query);
                DBObject search_query = docs.get(0);  //取第一条数据即可
                Gson gson = new Gson();
                NewsPage newspage = gson.fromJson(search_query.toString(), NewsPage.class);
                String publisher=newspage.getPublisher();
                map.put(collectionName,collectionName );
            }
        }
        return map;
    }
}
