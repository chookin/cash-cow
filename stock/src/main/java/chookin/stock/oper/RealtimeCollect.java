package chookin.stock.oper;

import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.pipeline.Pipeline;
import cmri.etl.spider.Spider;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class RealtimeCollect extends BaseOper {
    Set<Pipeline> pipelines = new HashSet<>();

    Map<String, StockEntity> stocks;

    public RealtimeCollect setStocks(Collection<StockEntity> stocks){
        if(this.stocks == null){
            this.stocks = new HashMap<>();
        }
        for(StockEntity stock: stocks){
            this.stocks.put(stock.getCode(), stock);
        }
        return this;
    }

    public RealtimeCollect addPipeline(Pipeline pipeline){
        this.pipelines.add(pipeline);
        return this;
    }

    public Map<String, StockEntity> getStocks(){
        if(this.stocks == null){
            return StockMapHandler.getStocksMap();
        }else {
            return this.stocks;
        }
    }

    @Override
    boolean action() {
        if (!processOption(OperName.CollectRealData)) {
            return false;
        }
        doWork();
        return true;
    }

    void doWork(){
        Map<String, StockEntity> stocks = getStocks();
        Spider spider = new Spider(OperName.CollectRealData)
                .addRequest(chookin.stock.extractor.sina.IndexPageProcessor.getRequest())
                ;
        this.pipelines.forEach(spider::addPipeline);
        for(StockEntity stock : stocks.values()){
            // contrast to qq's gtimg, sina is more real-time.
            // spider.addRequest(chookin.stock.extractor.gtimg.RealtimePageProcessor.getRequest(stock));
            spider.addRequest(chookin.stock.extractor.sina.RealtimePageProcessor.getRequest(stock));
        }
        spider.run();
    }
}
