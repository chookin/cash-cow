package chookin.stock.extractor.pipeline;

import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.repository.CompanyInfoRepository;
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
 * Created by zhuyin on 3/21/15.
 */
@Service
public class CompanyInfoPileline implements Pipeline {
    private static final Logger LOG = Logger.getLogger(CompanyInfoPileline.class);

    @Autowired
    private CompanyInfoRepository repository;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Set<CompanyInfoEntity> cache = new HashSet<>();

    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        CompanyInfoEntity entity = resultItems.getRequest().getExtra("company", CompanyInfoEntity.class);
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
        LOG.info("save "+ cache.size() + " stocks' company info");
    }
}
