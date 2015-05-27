package chookin.stock.extractor.pipeline;

import chookin.stock.orm.domain.HistoryEntity;
import chookin.stock.orm.repository.HistoryDataRepository;
import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class HistoryPipeline implements Pipeline {
    private static final Logger LOG = Logger.getLogger(HistoryPipeline.class);

    @Autowired
    private HistoryDataRepository repository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Set<HistoryEntity> cache = new HashSet<>();
    private int cacheSize = 100;
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        List<HistoryEntity> entity = (List<HistoryEntity>) resultItems.getField("histData");
        if(entity == null || entity.isEmpty()){
            return;
        }

        lock.writeLock().lock();
        try {
            cache.addAll(entity);
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
        LOG.info("save " + cache.size() + " stocks' history data");
    }
}
