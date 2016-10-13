package chookin.secondhand.utils;

import chookin.secondhand.model.TargetObject;
import cmri.etl.common.MapItem;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.dao.MongoHandler;
import cmri.utils.lang.SamplingUtils;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import java.util.Collection;

/**
 * Created by chookin on 16/10/12.
 */
public class EntityHelper {
    /**
     * 用于增量抓取时判定是否继续抓取
     *
     * @param entities 本次抓取时获取到的记录集合
     * @return true 如果继续抓取
     */
    public static boolean continueCrawl(TargetObject target, Collection<MapItem> entities) {
        Collection<MapItem> samples = SamplingUtils.sample(entities, 6);
        if (samples.isEmpty()) {
            return false;
        }
        if (!ConfigManager.getBool("chookin.incrementalCrawl")) {
            return true;
        }
        for (MapItem entity : samples) {
            if (!isEntityArchived(target, entity)) {
                return true;// 若有一个未被抓取,则继续
            }
        }// 所有样本记录在之前都被存储过,那意味着之后的页面记录都被抓取过了
        return false;
    }

    /**
     * 判断该数据记录是否已存储
     *
     * @param entity 数据记录
     * @return 如果已存储, 返回true; 否则,返回false.
     */
    public static boolean isEntityArchived(TargetObject target, MapItem entity) {
        String collection = (String) entity.get("collection");
        MongoHandler dao = MongoHandler.instance();
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.put("_id").is(getRecordId(target, String.valueOf(entity.get("id"))));
        DBObject obj = dao.findFirst(collection, queryBuilder.get());
        return obj != null;
    }

    /**
     * 获取帖子主题的存储id
     *
     * @param target 渠道,例如水木的二手电脑市场
     * @param topicId     帖子的id
     */
    public static String getRecordId(TargetObject target, String topicId) {
     return String.format("%s_%s_%s", target.getSite(), target.getCategory(), topicId);
    }
}
