package chookin.stock.oper;

import chookin.stock.extractor.pipeline.TradePipeline;
import chookin.stock.extractor.sina.HistDataDetailPageProcessor;
import chookin.stock.handler.HolidayHandler;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.pipeline.FilePipeline;
import cmri.etl.spider.Spider;
import cmri.etl.spider.SpiderAdapter;
import cmri.utils.lang.BaseOper;
import cmri.utils.lang.TimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by zhuyin on 5/20/15.
 */
@Service
public class TradeCollect extends BaseOper {
    @Autowired
    private TradePipeline pipeline;

    @Override
    public boolean action() {
        if (!getOptions().process(OperName.CollectHistDataDetail)) {
            return false;
        }
        String start = getOptions().get("start");
        String end = getOptions().get("end");
        Calendar startDay = TimeHelper.parseCalendar(start, "yyyy-MM-dd");
        Calendar endDay = TimeHelper.parseCalendar(end, "yyyy-MM-dd");

        Spider spider = new SpiderAdapter(OperName.CollectHistDataDetail)
                .setSleepMillis(Integer.MAX_VALUE)
                .addPipeline(pipeline)
                .addPipeline(new FilePipeline())
                ;
        Map<String, StockEntity> stocks = StockMapHandler.getStocksMap();
        for(Map.Entry<String, StockEntity> entry : stocks.entrySet()){
            addRequest(spider, entry.getValue(), startDay, endDay);
        }
        spider.run();
        return true;
    }

    private void addRequest(Spider spider, StockEntity stock, Calendar startDay, Calendar endDay){
        for ( Calendar curDay = (Calendar) startDay.clone(); curDay.compareTo(endDay) <= 0; curDay.add(Calendar.DAY_OF_YEAR, 1) ){
            if(HolidayHandler.isHoliday(curDay)){
                continue;
            }
            spider.addRequest(HistDataDetailPageProcessor.getRequest(stock, curDay.getTime()));
        }
    }
}
