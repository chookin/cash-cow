package chookin.stock.extractor.pipeline;

import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.StockRepository;
import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class StockPipelie implements Pipeline {
    private static final Logger LOG = Logger.getLogger(StockPipelie.class);

    @Autowired
    private StockRepository repository;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private Set<StockEntity> cache = new HashSet<>();
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        Map<String, StockEntity> entities = (Map<String, StockEntity>) resultItems.getField("stockCollection");
        lock.writeLock().lock();
        try {
            cache.addAll(entities.values());
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
        LOG.info("save "+cache.size()+" stocks");
    }
}
