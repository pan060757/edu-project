import mongodb.MongoDbManager;

/**
 * Created by song on 2015/12/24.
 */
public class ProviderMain {
//    public static void main(String[] args) throws UnknownHostException {
//        MongoDbManager mongoDbManager = MongoDbManager.getInstance();
//        String collectionName = Config.NEWS_PAGES_COLLECTION_NAME;
//
////        DBCollection collection =mongoDbManager.getDBCollection(collectionName);
////        DBObject query=new BasicDBObject("title","美华裔入选名厨永久邮票 推动中餐引入美国");
////        DBCursor cur =collection.find(query);
////        while(cur.hasNext()){
////            DBObject document = cur.next();
////            System.out.println(document);
////        }
//
////        //select
//        DBObject doc2 = null;
//        DBObject query = new BasicDBObject();
//        query.put("url", "http://www.ahedu.gov.cn/26/view/284127.shtml");
//
//        List<DBObject> docs = mongoDbManager.selectDocuments(collectionName,query);
//        for (int i = 0; i < docs.size(); i++) {
//            System.out.println(docs.get(i));
//        }
//
//     //   update
//        DBObject updatedDocument = new BasicDBObject();
//        updatedDocument.put("$set", new BasicDBObject().append("num", 100));
//        boolean result =mongoDbManager.updateDocument(collectionName, query, updatedDocument);
//        System.out.println(result);
//        query.put("num", 100);
////        //remove
////        boolean result = m.deleteDocument(collectionName, query);
////        System.out.println(result);
//    }
}
