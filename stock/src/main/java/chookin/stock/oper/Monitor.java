package chookin.stock.oper;

import chookin.stock.handler.HolidayHandler;
import chookin.stock.handler.StockMapHandler;
import chookin.stock.orm.domain.HoldingsEntity;
import chookin.stock.orm.domain.IndexEntity;
import chookin.stock.orm.domain.RealtimeEntity;
import chookin.stock.orm.repository.HoldingsRepository;
import chookin.stock.utils.SpringHelper;
import cmri.etl.pipeline.Pipeline;
import cmri.utils.concurrent.ThreadHelper;
import cmri.utils.concurrent.ThreadHelper.Status;
import cmri.utils.lang.TimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

/**
 * Created by zhuyin on 6/13/15.
 */
@Service
public class Monitor extends BaseOper implements Runnable{
    private static final String sep = "\t";
    @Autowired
    private RealtimeCollect realtimeCollect;
    private int interval = 1000; // milliseconds
    private Status stat = Status.Init;

    public void start(){
        Thread thread = new Thread(this);
        thread.setDaemon(false);
        thread.start();
    }

    public void stop(){
        this.stat = Status.Stopped;
    }

    private void checkIfRunning() {
        if (stat == Status.Running) {
            throw new IllegalStateException("Spider is already running!");
        }
    }

    private void init(){
        HoldingsRepository repository = (HoldingsRepository) SpringHelper.getAppContext().getBean("holdingsRepository");
        List<HoldingsEntity> holdingsCollection = repository.findByValid(true);
        Map<String, HoldingsEntity> holdingsMap = new HashMap<>();
        for(HoldingsEntity entity: holdingsCollection){
            holdingsMap.put(entity.getStockCode(), entity);
        }
        realtimeCollect.setStocks(StockMapHandler.getStocksMap(holdingsMap.keySet()).values());
        interval = Integer.parseInt(getOptionParser().getOption("--interval", String.valueOf(interval)));
        realtimeCollect.addPipeline((Pipeline) SpringHelper.getAppContext().getBean("realtimePipeline"));
        realtimeCollect.addPipeline(resultItems -> {
            IndexEntity index = (IndexEntity) resultItems.getField("index");
            if(index != null){
                getLogger().info(getOut(index));
                return;
            }
            RealtimeEntity realtime = (RealtimeEntity) resultItems.getField("realData");
            if(realtime == null){
                return;
            }
            HoldingsEntity holdings = holdingsMap.get(realtime.getStockCode());
            getLogger().info(getOut(holdings, realtime));
        });
    }

    private Double getEarn(HoldingsEntity holdings, RealtimeEntity realtime){
        if(holdings.getPrice() != null && realtime.getCurPrice() != null)
            return holdings.getHand() * 100 * (realtime.getCurPrice() - holdings.getPrice());
        return null;
    }

    private Double getChangeRatio(RealtimeEntity realtime){
        if(realtime.getYclose() != null && realtime.getCurPrice() != null)
            return (realtime.getCurPrice() - realtime.getYclose()) / realtime.getYclose() * 100;
        return null;
    }

    private String getOut(IndexEntity index){
        return new StringBuilder(index.getCode()).append(sep)
                .append(TimeHelper.toString(index.getTime(), "yyyy-MM-dd HH:mm:ss")).append(sep)
                .append(index.getPoint()).append(sep)
                .append(index.getChangeRatio()).append("%").append(sep)
                .append(index.getTradeHand()).append(sep)
                .append(index.getTradeValue()).append(sep)
                .toString();
    }
    private String getOut(HoldingsEntity holdings, RealtimeEntity realtime){
        Double earn = getEarn(holdings, realtime);
        if(earn == null){
            return null;
        }
        Double earnRatio = earn / (holdings.getPrice() * holdings.getHand() * 100);
        return new StringBuilder(realtime.getStockCode()).append(sep)
                .append(TimeHelper.toString(realtime.getTime(), "yyyy-MM-dd HH:mm:ss")).append(sep)
                .append(realtime.getOpen()).append(sep)
                .append(realtime.getYclose()).append(sep)
                .append(realtime.getHighPrice()).append(sep)
                .append(realtime.getLowPrice()).append(sep)
                .append(realtime.getCurPrice()).append(sep)
                .append(String.format("%.2f", getChangeRatio(realtime))).append("%").append(sep)
                .append(holdings.getPrice()).append(sep)
                .append(holdings.getHand()).append(sep)
                .append(String.format("%.2f", earn)).append(sep)
                .append(String.format("%.2f", earnRatio * 100)).append("%").append("\n")
                .append(realtime.getBuys()).append("\n")
                .append(realtime.getSells())
                .toString();
    }
    @Override
    boolean action() {
        start();
        return true;
    }

    @Override
    public void run() {
        try {
            long count = 0;
            onStart();
            while (!Thread.currentThread().isInterrupted() && stat == Status.Running) {
                if(count > 0){
                    ThreadHelper.sleep(interval);
                }
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                if(HolidayHandler.isHoliday(now)){
                    continue;
                }
                if(isRecess(LocalTime.now())){
                    if(count > 0){ //如果是首次，则获取
                        continue;
                    }
                }
                realtimeCollect.doWork();
                if(++count == Long.MAX_VALUE){
                    count = 1;
                }
            }
        } catch (Throwable e) {
            getLogger().error(null, e);
        }finally {
            onStop();
        }
    }

    private boolean isRecess(LocalTime time){
        if(time.isBefore(LocalTime.of(9, 30))){
            return true;
        }
        if(time.isAfter(LocalTime.of(11, 30)) && time.isBefore(LocalTime.of(13, 0))){
            return true;
        }
        if(time.isAfter(LocalTime.of(15, 0))){
            return true;
        }
        return false;
    }
    private void onStart(){
        checkIfRunning();
        init();
        stat = Status.Running;
    }

    private void onStop() {
        stat = Status.Stopped;
    }

    public static void main(String[] args){
        Monitor monitor = (Monitor) SpringHelper.getAppContext().getBean("monitor");
        monitor.setArgs(args).action();
    }
}
