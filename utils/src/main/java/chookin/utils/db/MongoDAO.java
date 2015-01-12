package chookin.utils.db;

import chookin.utils.configuration.ConfigManager;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created by zhuyin on 12/15/14.
 */
public abstract class MongoDAO {
    private static final Logger LOG = Logger.getLogger(MongoDAO.class);
    private static String database = ConfigManager.getProperty("mongo.database");
    private Mongo mongo;
    public Mongo getMongo(){
        if(mongo != null){
            return mongo;
        }
        String host = ConfigManager.getProperty("mongo.host");
        int port = ConfigManager.getPropertyAsInteger("mongo.port");
        String user = ConfigManager.getProperty("mongo.user");

        try {
            String pwd = ConfigManager.getProperty("mongo.password");
            if(user == null || user.isEmpty()){
                this.mongo = new MongoClient(host, port);
            }else {
                ServerAddress serverAddress = new ServerAddress(host, port);
                // TODO add user pwd support
                this.mongo = new MongoClient(serverAddress);
            }
            this.mongo.isLocked();
        } catch (MongoTimeoutException mt){
            LOG.error(mt.toString());
            LOG.trace("retry connect mongodb after 5 seconds");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                LOG.warn(null, e);
            }
            this.mongo = null;
            return this.getMongo();
        } catch (UnknownHostException e) {
            LOG.fatal(null, e);
            System.exit(-1);
        }
        return this.mongo;
    }
    public DB getDB(){
        return this.getMongo().getDB(database); // E-commerce
    }
    protected abstract DBCollection getCollection();

    public void dropField(String field){
        DBCollection dbCollection = this.getCollection();
        dbCollection.update(new BasicDBObject(), new BasicDBObject("$unset", new BasicDBObject(field, 1)), false, true);
    }
}

