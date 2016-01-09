package chookin.house.service.base;

import chookin.house.HouseDAO;
import chookin.house.HousePipeline;
import cmri.etl.common.Request;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.scheduler.PriorityScheduler;
import cmri.etl.scheduler.Scheduler;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;

import java.util.Set;

/**
 * Created by zhuyin on 3/28/15.
 */
public interface HouseCollect {
    default boolean collectHouses() {
        HouseDAO dao = HouseDAO.getInstance();
        try {
            new Spider(getSiteName(), getScheduler())
                    .addRequest(getSeedRequests())
                    .addPipeline(new HousePipeline())
                    .addPipeline(new FilePipeline())
                    .setSleepMillisecond(ConfigManager.getPropertyAsInteger("download.sleepMilliseconds"))
                    .thread(ConfigManager.getPropertyAsInteger("download.concurrent.num"))
                    .setTimeOut(ConfigManager.getPropertyAsInteger("download.timeout"))
                    .setValidateSeconds(ConfigManager.getPropertyAsLong("page.validPeriod"))
                    .setUserAgent(getUserAgent())
                    .run();
            return true;
        } finally {
            dao.close();
        }
    }

    String getSiteName();

    Set<Request> getSeedRequests();

    default Scheduler getScheduler(){
        return new PriorityScheduler();
    }

    default String getUserAgent(){
        return ConfigManager.getProperty("web.userAgent");
    }
}
