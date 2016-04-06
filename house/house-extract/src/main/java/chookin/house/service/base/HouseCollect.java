package chookin.house.service.base;

import chookin.house.HouseDAO;
import chookin.house.HousePipeline;
import cmri.etl.common.Request;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.scheduler.PriorityScheduler;
import cmri.etl.scheduler.Scheduler;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.MapAdapter;

import java.util.Set;

/**
 * Created by zhuyin on 3/28/15.
 */
public interface HouseCollect {
    default boolean collectHouses() {
        HouseDAO dao = HouseDAO.getInstance();
        try {
            new SpiderAdapter(getSiteName(), new MapAdapter<>("scheduler", "cmri.etl.scheduler.PriorityScheduler").get())
                    .addRequest(getSeedRequests())
                    .addPipeline(new HousePipeline())
                    .addPipeline(new FilePipeline())
                    .run();
            return true;
        } finally {
            dao.close();
        }
    }

    String getSiteName();

    Set<Request> getSeedRequests();
}
