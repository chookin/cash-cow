package chookin.utils.db;

import chookin.utils.concurrent.ThreadHelper;
import chookin.utils.configuration.ConfigManager;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuyin on 12/15/14.
 */
public abstract class MongoDAO<T> {
    private static final Logger LOG = Logger.getLogger(MongoDAO.class);
    private static final String database = ConfigManager.getProperty("mongo.database");
    private static final Lock poolLock = new ReentrantLock();
    private static final Queue<Mongo> pool = new ArrayDeque<>();

    private Mongo mongo = null;
    private Mongo getMongo(){
        if(mongo == null){
            mongo = findOrCreateMongo();
        }
        return mongo;
    }

    /**
     * Must call this method to recycle the Mongo instance(MongoClient).
     * @return this
     */
    public MongoDAO close(){
        if(this.mongo == null){
            return this;
        }
        poolLock.lock();
        try{
            pool.add(mongo);
            mongo = null;
        }finally {
            poolLock.unlock();
        }
        return this;
    }
    private static Mongo findOrCreateMongo() {
        poolLock.lock();
        try {
            if (pool.isEmpty()) {
                return createMongo();
            }else{
                return pool.remove();
            }
        }finally {
            poolLock.unlock();
        }
    }
    private static Mongo createMongo(){
        Mongo mongo = null;
        String host = ConfigManager.getProperty("mongo.host");
        int port = ConfigManager.getPropertyAsInteger("mongo.port");
        String user = ConfigManager.getProperty("mongo.user");

        try {
            String pwd = ConfigManager.getProperty("mongo.password");
            if(user == null || user.isEmpty()){
                mongo = new MongoClient(host, port);
            }else {
                ServerAddress serverAddress = new ServerAddress(host, port);
                // TODO add user pwd support
                mongo = new MongoClient(serverAddress);
            }
            mongo.isLocked();
        } catch (MongoTimeoutException mt){
            LOG.error(mt.toString());
            LOG.warn("retry connect mongodb after 5 seconds");
            ThreadHelper.sleep(5000);
            return createMongo();
        } catch (UnknownHostException e) {
            LOG.fatal(null, e);
            System.exit(-1);
        }
        return mongo;
    }

    public DB getDB(){
        return this.getMongo().getDB(database);
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
    public int save(T entity){
        List<T> entities = new ArrayList<>();
        entities.add(entity);
        return save(entities);
    }

    public void dropField(String field, DBObject query){
        DBCollection dbCollection = this.getCollection();
        dbCollection.update(query, new BasicDBObject("$unset", new BasicDBObject(field, 1)), false, true);
    }

    public abstract DBCollection getCollection();
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

    /**
     * update by _id
     * @param entity
     * @return
     */
    public int update(T entity){
        DBCollection dbCollection = this.getCollection();
        WriteResult writeResult = dbCollection.update(new BasicDBObject("_id", get_id(entity)), getBasicDBObject(entity), true, false);
        return writeResult.getN();
    }

    /**
     * update by _id. Use BulkWriteOperation, remove the old and insert new.
     * @param entities
     * @return
     */
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
        LOG.trace("Mongo update: " + result);
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
        LOG.trace("Mongo update: " + result);
        return result.getInsertedCount();
    }
}

