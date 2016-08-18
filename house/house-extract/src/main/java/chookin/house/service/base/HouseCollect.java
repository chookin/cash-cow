package chookin.house.service.base;

import chookin.house.HousePipeline;
import cmri.etl.common.Request;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.MapAdapter;

import java.util.Set;

/**
 * Created by zhuyin on 3/28/15.
 */
public interface HouseCollect {
    default boolean collectHouses() {
        new SpiderAdapter(getSiteName(), new MapAdapter<>("scheduler", "cmri.etl.scheduler.PriorityScheduler").get())
                .addRequest(getSeedRequests())
                .addPipeline(new HousePipeline())
                .addPipeline(new FilePipeline())
                .run();
        return true;
    }

    String getSiteName();

    Set<Request> getSeedRequests();
}
