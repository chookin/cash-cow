package chookin.stock.extractor.pipeline;

import chookin.stock.orm.domain.HistoryDayDetailEntity;
import chookin.stock.orm.repository.TradeReposity;
import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 5/21/15.
 */
@Service
public class TradePipeline implements Pipeline {
    private static final Logger LOG = Logger.getLogger(HistDataPipeline.class);

    @Autowired
    private TradeReposity repository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Set<HistoryDayDetailEntity> cache = new HashSet<>();
    private int cacheSize = 1000;
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        HistoryDayDetailEntity entity = resultItems.getRequest().getExtra("trade", HistoryDayDetailEntity.class);
        lock.writeLock().lock();
        try {
            cache.add(entity);
            if(cache.size() >= cacheSize){
                saveCache();
                cache.clear();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void close() throws IOException {
        lock.readLock().lock();
        try {
            saveCache();
        }finally {
            lock.readLock().unlock();
        }
    }
    @Transactional
    private void saveCache(){
        this.repository.save(cache);
        LOG.info("save "+ cache.size()+" stocks' history data");
    }
}