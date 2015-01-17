package chookin.utils.db;

import chookin.utils.configuration.ConfigManager;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by zhuyin on 12/15/14.
 */
public abstract class MongoDAO<T> {
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
            LOG.warn("retry connect mongodb after 5 seconds");
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

    /**
     * If exist a item equals that to save, then not save it.
     * @param entities
     * @return
     */
    public int save(Collection<T> entities){
        int count = 0;
        DBCollection dbCollection = this.getCollection();
        for(T entity: entities) {
            T saved = findFirst(new BasicDBObject("_id", get_id(entity)));
            if (!entity.equals(saved)) {
                dbCollection.save(getBasicDBObject(entity));
                ++count;
            }
        }
        return count;
    }

    public void dropField(String field){
        DBCollection dbCollection = this.getCollection();
        dbCollection.update(new BasicDBObject(), new BasicDBObject("$unset", new BasicDBObject(field, 1)), false, true);
    }

    protected abstract DBCollection getCollection();
    protected abstract String get_id(T entity);
    protected abstract BasicDBObject getBasicDBObject(T entity);
    protected abstract T parse(DBObject dbObject);

    public List<T> find(Map<String, Object> kv){
        QueryBuilder queryBuilder = new QueryBuilder();
        for(Map.Entry<String, Object> pair : kv.entrySet()){
            queryBuilder.put(pair.getKey()).is(pair.getValue());
        }
        return find(queryBuilder.get());
    }

    public List<T> find(DBObject ref){
        List<T> entities = new ArrayList<>();
        DBCursor cursor = this.getCollection().find(ref);
        while (cursor.hasNext()){
            DBObject item = cursor.next();
            entities.add(parse(item));
        }
        return entities;
    }
    public T findFirst(DBObject ref){
        DBCursor cursor = this.getCollection().find(ref);
        while (cursor.hasNext()){
            DBObject item = cursor.next();
            return parse(item);
        }
        return null;
    }
    public List<T> findExistField(String field, boolean isWith){
        QueryBuilder queryBuilder = new QueryBuilder().put(field).exists(isWith);
        return find(queryBuilder.get());
    }
    public int update(T entity){
        DBCollection dbCollection = this.getCollection();
        WriteResult writeResult = dbCollection.update(new BasicDBObject("_id", get_id(entity)), getBasicDBObject(entity));
        return writeResult.getN();
    }

    public int update(Collection<T> entities){
        if(entities.isEmpty()){
            return 0;
        }
        DBCollection dbCollection = this.getCollection();
        BulkWriteOperation bulkop=dbCollection.initializeOrderedBulkOperation();
        for(T entity: entities) {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.put("_id").is(get_id(entity));
            bulkop.find(queryBuilder.get()).removeOne(); // remove and then insert
            bulkop.insert(getBasicDBObject(entity));
        }
        BulkWriteResult result=bulkop.execute();
        LOG.trace(result);
        return result.getInsertedCount();
    }

    /**
     * @param entities
     * @return
     * @throws BulkWriteException if duplicate key would happens.
     */
    public int insert(Collection<T> entities){
        if(entities.isEmpty()){
            return 0;
        }
        DBCollection dbCollection = this.getCollection();
        BulkWriteOperation bulkop=dbCollection.initializeUnorderedBulkOperation();
        for(T entity: entities) {
            bulkop.insert(getBasicDBObject(entity));
        }
        BulkWriteResult result=bulkop.execute();
        LOG.trace(result);
        return result.getInsertedCount();
    }
}

