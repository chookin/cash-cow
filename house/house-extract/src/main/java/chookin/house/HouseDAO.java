package chookin.house;

import cmri.utils.dao.MongoDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by zhuyin on 3/28/15.
 */
public class HouseDAO extends MongoDAO<House> {
    private static HouseDAO dao = new HouseDAO();
    public static HouseDAO getInstance(){
        return dao;
    }
    private HouseDAO(){
        super("house");
    }

    @Override
    protected String getId(House entity) {
        return entity.getCode();
    }

    @Override
    protected BasicDBObject getBasicDBObject(House entity) {
        BasicDBObject doc = new BasicDBObject();
        doc.put("_id", getId(entity));
        doc.put("name", entity.getName());
        doc.put("site", entity.getSite());
        doc.put("code", entity.getCode());
        doc.put("url", entity.getUrl());
        doc.put("time", entity.getTime());

        if(!entity.getProperties().isEmpty())
            doc.put("properties", entity.getProperties());
        return doc;
    }

    @Override
    public House parse(DBObject dbObject) {
        return new House()
                .setName((String) dbObject.get("name"))
                .setCode((String) dbObject.get("code"))
                .setSite((String) dbObject.get("site"))
                .setUrl((String) dbObject.get("url"))
                .setTime((Date) dbObject.get("time"))
                .set((Map<String, Object>) dbObject.get("properties"))
                ;
    }
}
