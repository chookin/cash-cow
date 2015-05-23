package chookin.stock.extractor.pipeline;

import chookin.stock.orm.domain.RealDataEntity;
import chookin.stock.orm.repository.RealDataRepository;
import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/22/15.
 */
@Service
public class RealDataPipeline implements Pipeline {
    @Autowired
    private RealDataRepository repository;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<RealDataEntity> cache = new HashSet<>();
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        RealDataEntity entity = resultItems.getRequest().getExtra("realData", RealDataEntity.class);
        lock.writeLock().lock();
        try {
            cache.add(entity);
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
    }
}
