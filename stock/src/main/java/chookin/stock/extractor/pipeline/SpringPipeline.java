package chookin.stock.extractor.pipeline;

import cmri.etl.common.MapItem;
import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 6/28/15.
 */
public abstract class SpringPipeline<T, ID extends Serializable> implements Pipeline {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Set<T> cache = new HashSet<>();
    private final Object master;
    public SpringPipeline(Object master){
        this.master = master;
    }
    @Override
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        Collection<MapItem> entities = resultItems.getItems();
        lock.writeLock().lock();
        try {
            for(MapItem obj: entities){
                T entity = (T) obj;
                // TODO
                cache.add(entity);
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

    protected abstract CrudRepository<T, ID> getRepository();
    @Transactional
    private void saveCache(){
        getRepository().save(cache);
        getLogger().info("save "+cache.size()+" " + master);
    }
}
