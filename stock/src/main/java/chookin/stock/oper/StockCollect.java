package chookin.stock.oper;

import chookin.stock.extractor.eastmoney.StockPageProcessor;
import chookin.stock.extractor.pipeline.StockPipelie;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.domain.StockMap;
import cmri.etl.common.Request;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.utils.configuration.ConfigManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Created by zhuyin on 3/22/15.
 */
public class StockCollect extends BaseOper {
    @Autowired
    private StockPipelie pipeline;

    @Override
    boolean action() throws IOException {
        String option = "collect-stock";
        if (!processOption(option)) {
            return false;
        }
        return collect();
    }

    private boolean collect() {
        Map<String, StockEntity> stocks = StockMap.getStocksMap();

        Spider spider = new Spider("company-info")
                .setDownloader(JsoupDownloader.getInstance())
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                .setSleepMillisecond(ConfigManager.getPropertyAsInteger("download.sleepMilliseconds"))
                .thread(ConfigManager.getPropertyAsInteger("download.concurrent.num"))
                .setTimeOut(ConfigManager.getPropertyAsInteger("download.timeout"))
                .setValidateSeconds(ConfigManager.getPropertyAsLong("page.validPeriod"))
                .addRequest(
                        new Request("http://quote.eastmoney.com/stocklist.html")
                                .setPageProcessor(new StockPageProcessor())
                                .putExtra("existedStocks", stocks)
                );

        spider.run();
        return true;
    }
}
