package chookin.etl.dao;

import chookin.etl.common.Proxy;
import chookin.utils.db.MongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import java.util.Date;

/**
 * Created by zhuyin on 3/5/15.
 */
public class ProxyDAO extends MongoDAO<Proxy> {
    private static final String CollectionName = "proxy";
    private ProxyDAO(){}
    public static ProxyDAO getInstance(){return new ProxyDAO();}
    @Override
    public DBCollection getCollection() {
        return this.getDB().getCollection(CollectionName);
    }

    @Override
    protected String get_id(Proxy entity) {
        return String.format("%s-%d", entity.getHost(), entity.getPort());
    }

    @Override
    protected BasicDBObject getBasicDBObject(Proxy entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", get_id(entity));
        doc.put("type", entity.getType());
        doc.put("highAnonymity", entity.isHighAnonymity());

        doc.put("host", entity.getHost());
        doc.put("port", entity.getPort());

        doc.put("user", entity.getUser());
        doc.put("passwd", entity.getPasswd());
        doc.put("desc", entity.getDesc());
        doc.put("country", entity.getCountry());
        doc.put("location", entity.getLocation());
        doc.put("accessTime", entity.getAccessTime());
        doc.put("connectTime", entity.getConnectTime());

        doc.put("validateTime", entity.getValidateTime());
        return doc;
    }

    @Override
    protected Proxy parse(DBObject dbObject) {
        return new Proxy()
                .setType((String) dbObject.get("type"))
                .setHighAnonymity((Boolean) dbObject.get("highAnonymity"))
                .setHost((String) dbObject.get("host"))
                .setPort((Integer) dbObject.get("port"))
                .setUser((String) dbObject.get("user"))
                .setPasswd((String) dbObject.get("passwd"))
                .setDesc((String) dbObject.get("desc"))
                .setCountry((String) dbObject.get("country"))
                .setLocation((String) dbObject.get("location"))
                .setAccessTime((Double) dbObject.get("accessTime"))
                .setConnectTime((Double) dbObject.get("connectTime"))
                .setValidateTime((Date) dbObject.get("validateTime"));
    }
}
