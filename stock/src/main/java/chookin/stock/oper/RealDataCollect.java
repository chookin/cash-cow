package chookin.stock.oper;

import chookin.stock.extractor.gtimg.RealDataPageProcessor;
import chookin.stock.extractor.pipeline.RealTimePipeline;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.downloader.JsoupDownloader;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class RealDataCollect extends BaseOper {
    @Autowired
    private RealTimePipeline pipeline;

    @Override
    boolean action() {
        if (!processOption(OperName.CollectRealData)) {
            return false;
        }
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();

        Spider spider = new Spider(OperName.CollectRealData)
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                ;

        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            spider.addRequest(RealDataPageProcessor.getRequest(entry.getValue()));
        }
        spider.run();
        return true;
    }
}
