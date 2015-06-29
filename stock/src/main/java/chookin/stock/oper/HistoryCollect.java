package chookin.stock.oper;

import chookin.stock.extractor.pipeline.HistoryPipeline;
import chookin.stock.extractor.sina.HistDataPageProcessor;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import chookin.stock.utils.SpringHelper;
import cmri.etl.monitor.SpiderMonitor;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.utils.lang.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.JMException;
import java.util.Map;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class HistoryCollect extends BaseOper {
    @Autowired
    private HistoryPipeline pipeline;

    @Override
    boolean action() {
        if (!processOption(OperName.CollectHistData)) {
            return false;
        }
        doWork();
        return true;
    }

    void doWork(){
        Spider spider = new Spider(OperName.CollectHistData)
                .setValidateMilliseconds(DateHelper.DAY_MILLISECONDS)
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                ;
        try {
            SpiderMonitor.instance().register(spider);
        } catch (JMException e) {
            getLogger().error(null, e);
        }
        addRequest(spider);
        spider.run();
    }

    private void addRequest(Spider spider){
        // 当前季度
        String toQuarter = String.format("%d-%d", DateHelper.getCurrentYear(), DateHelper.getCurrentQuarter());
        String[] startArr = getOptionParser().getOption("start", toQuarter).split("-");
        String[] endArr = getOptionParser().getOption("end", toQuarter).split("-");
        if(startArr.length != 2){
            throw new IllegalArgumentException();
        }
        if(endArr.length != 2){
            throw new IllegalArgumentException();
        }
        int startYear = Integer.parseInt(startArr[0]);
        int startQuarter = Integer.parseInt(startArr[1]);
        int endYear = Integer.parseInt(endArr[0]);
        int endQuarter = Integer.parseInt(endArr[1]);
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            addRequest(spider, entry.getValue(), startYear, startQuarter, endYear, endQuarter);
        }
    }
    private void addRequest(Spider spider, StockEntity stock, int year, int quarter){
        spider.addRequest(HistDataPageProcessor.getRequest(stock, year, quarter));
    }

    private void addRequest(Spider spider, StockEntity stock, int startYear, int startQuarter, int endYear, int endQuater) {
        if(startYear > endYear){
            return;
        }
        if(startYear == endYear){
            for(int quarter = startQuarter; quarter <= endQuater; ++quarter){
                addRequest(spider, stock, startYear, quarter);
            }
            return;
        }
        for(int quarter = startQuarter; quarter <= 4; ++quarter){
            addRequest(spider, stock, startYear, quarter);
        }
        for(int year = startYear + 1; year < endYear; ++year){
            for(int quarter = 1; quarter <= 4; ++quarter){
                addRequest(spider, stock, year, quarter);
            }
        }
        for(int quarter = 1; quarter <= endQuater;++quarter ){
            addRequest(spider, stock, endYear, quarter);
        }
    }

    public static void main(String[] args){
        HistoryCollect oper = (HistoryCollect) SpringHelper.getAppContext().getBean("historyCollect");
        oper.setArgs(args);
        oper.doWork();
    }
}
