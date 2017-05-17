package test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import mongodb.MongoDbManager;
import net.sf.json.JSONObject;

import java.util.HashMap;

/**
 * Created by song on 2016/1/13.
 */
public class CreateCollection {
    static MongoDbManager mongoDbManager =new MongoDbManager();
    static String collectionName ="dataSource";
    public static void main(String args[])
    {
//        ArrayList<String>list= FileUtils.readByLine("C:\\Users\\song\\Documents\\MobaXterm\\home\\.ssh\\edu-project\\data-provider\\sourceCategory.txt");
//        for(String collection:list)
//        {
//            String str[]=collection.split("\t");
//            DBObject object=new BasicDBObject();
//            object.put("sourceId",str[0]);
//            object.put("sourceName",str[1]);
//            object.put("categoryId", str[2]);
//            object.put("categoryName",str[3]);
//            mongoDbManager.insertDocument(collectionName,object);
//        }
        getsourceCategoryById();
    }
    public static HashMap<String,String> getsourceCategoryById()
    {
        HashMap<String,String> map=new HashMap<String,String>();
        DBObject relation_query = new BasicDBObject();
        DBCollection collection = mongoDbManager.getDBCollection(collectionName);
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject search_query = cursor.next();
            JSONObject jsonObj= JSONObject.fromObject(search_query);
            if(jsonObj.containsKey("sourceId")||jsonObj.containsKey("categoryName")) {
                map.put(jsonObj.getString("sourceId"), jsonObj.getString("categoryName"));
                System.out.println(jsonObj.getString("sourceId")+"\t"+jsonObj.getString("categoryName"));
            }
        }
        return map;
    }
}
