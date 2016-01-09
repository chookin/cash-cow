package chookin.stock.oper;

import chookin.stock.extractor.eastmoney.StockPageProcessor;
import chookin.stock.extractor.pipeline.StockPipeline;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.utils.SpringHelper;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.BaseOper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class StockCollect extends BaseOper {
    @Autowired
    private StockPipeline pipeline;

    @Override
    public boolean action() {
        if (!getOptions().process(OperName.CollectStock)) {
            return false;
        }
        doWork();
        return true;
    }

    private void doWork(){
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();

        new SpiderAdapter(OperName.CollectStock)
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                .addRequest(StockPageProcessor.getRequest(stocks))
                .run();
    }

    public static void main(String[] args){
        StockCollect oper = (StockCollect) SpringHelper.getAppContext().getBean("stockCollect");
        oper.doWork();
    }
}
