package chookin.stock.oper;

import chookin.stock.extractor.eastmoney.StockPageProcessor;
import chookin.stock.extractor.pipeline.StockPipelie;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.utils.SpringHelper;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class StockCollect extends BaseOper {
    @Autowired
    private StockPipelie pipeline;

    @Override
    boolean action() {
        if (!processOption(OperName.CollectStock)) {
            return false;
        }
        doWork();
        return true;
    }
    private void doWork(){
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();

        Spider spider = new Spider(OperName.CollectStock)
                .setDownloader(JsoupDownloader.getInstance())
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                .setSleepMillisecond(ConfigManager.getPropertyAsInteger("download.sleepMilliseconds"))
                .thread(ConfigManager.getPropertyAsInteger("download.concurrent.num"))
                .setTimeOut(ConfigManager.getPropertyAsInteger("download.timeout"))
                .setValidateSeconds(ConfigManager.getPropertyAsLong("page.validPeriod"))
                .addRequest(StockPageProcessor.getRequest(stocks));

        spider.run();
    }

    public static void main(String[] args){
        StockCollect oper = (StockCollect) SpringHelper.getAppContext().getBean("stockCollect");
        oper.doWork();
    }
}
