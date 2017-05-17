package mongodb;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.MongoOptions;
import java.util.ResourceBundle;

/**
 * Created by binbin on 16/1/10.
 */
public class MongoManager {

    private static MongoManager mongoManager;
    private Mongo mongo;

    static {

        mongoManager = new MongoManager();
    }

    public MongoManager(){

        ResourceBundle rb = ResourceBundle.getBundle("db");
        String host = rb.getString("ServerAddrIP");// 主机名
        int port = Integer.parseInt(rb.getString("mongoServerPort"));// 端口
        int poolSize = Integer.parseInt(rb.getString("poolSize"));// 连接数量
        int blockSize = Integer.parseInt(rb.getString("blockSize")); // 等待队列长度
        // 其他参数根据实际情况进行添加
        try {
            mongo = new Mongo(host, port);
            MongoOptions opt = mongo.getMongoOptions();
            opt.connectionsPerHost = poolSize;
            opt.threadsAllowedToBlockForConnectionMultiplier = blockSize;
        } catch (MongoException e) {
            e.printStackTrace();
        }

    }

    public final static MongoManager getInstance() {
        return mongoManager;
    }

    public final DB getDB(String dbName) {

        return mongo.getDB(dbName);

    }



}