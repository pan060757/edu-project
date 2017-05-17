package edu.ecnu.storage;

import com.mongodb.*;
import com.mongodb.util.JSON;
import edu.ecnu.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
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
    private String mongoServerAddr=null;;
    private int mongoServerPort;
    private static Map<String, DBCollection> dbCollectionMap = new ConcurrentHashMap<String, DBCollection>();

    private MongoDbManager() {
        // 配置默认服务器的IP地址和端口以及所使用的数据库
        mongoServerAddr=rb.getString("ServerAddrIP");
        mongoServerPort = Integer.valueOf(rb.getString("mongoServerPort"));
        DBNAME= rb.getString("DBNAME");
        init();
    }


    public void addIndexes(String collectionName,int desc, String... keyName){
        for (int i = 0; i <keyName.length ; i++) {
            DBObject index = new BasicDBObject(keyName[i],desc);
            addIndex(collectionName,index);
        }
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
    private DBCollection getDBCollection(String collectionName) {
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
     */
    public void addIndex(String collectionName, DBObject index){
        DBCollection collection =this.getDBCollection(collectionName);
        collection.createIndex(index);
    }
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

    public static void main(String[] args) throws UnknownHostException {
        MongoDbManager mongoDbManager = MongoDbManager.getInstance();
        String collectionName = "test";


        for (int i = 0; i <100 ; i++) {
            DBObject query = new BasicDBObject().append("index",i);
            mongoDbManager.insertDocument(collectionName,query);
        }
        
//        //select
//        DBObject doc2 = null;
        DBObject query = new BasicDBObject();
        query.put("index", "12");

        List<DBObject> docs = mongoDbManager.selectDocuments(collectionName,query);
        for (int i = 0; i < docs.size(); i++) {
            System.out.println(docs.get(i));
        }

        DBObject index = new BasicDBObject();
        index.put("index",1);
        mongoDbManager.addIndex(collectionName, index);


        DBObject query1 = new BasicDBObject();
        query.put("index", "12");

        List<DBObject> docs1 = mongoDbManager.selectDocuments(collectionName,query1);
        for (int i = 0; i < docs1.size(); i++) {
            System.out.println(docs1.get(i));
        }
        //update
//        DBObject updatedDocument = new BasicDBObject();
//        updatedDocument.put("$set", new BasicDBObject().append("num", 100));
//        boolean result = m.updateDocument(collectionName, query, updatedDocument);
//        System.out.println(result);
//        query.put("num", 100);
//        //remove
//        boolean result = m.deleteDocument(collectionName, query);
//        System.out.println(result);
    }
}
