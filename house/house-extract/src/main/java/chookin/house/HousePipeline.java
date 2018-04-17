package chookin.house;

import cmri.etl.common.ResultItems;
import cmri.etl.pipeline.Pipeline;
import cmri.utils.concurrent.ThreadHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/28/15.
 */
public class HousePipeline implements Pipeline {
    private static final Logger LOG = LoggerFactory.getLogger(HousePipeline.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Set<House> cache = new HashSet<>();
    private Set<String> sites = new HashSet<>();
    private Thread watchDaemon;
    private final ReadWriteLock daemonLock = new ReentrantReadWriteLock();
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    @SuppressWarnings("unchecked")
    public void process(ResultItems resultItems) {
        if (resultItems.isSkip()) {
            return;
        }
        House house = (House) resultItems.getField("house");
        if (house == null) {
            return;
        }
        lock.writeLock().lock();
        try {
            cache.add(house);
            String site = house.getSite();
            if (site != null) {
                sites.add(site);
            }
        } finally {
            lock.writeLock().unlock();
        }

        if (watchDaemon == null) {
            startWatchDaemon();
        }
    }

    @Override
    public void close() {
        HouseDAO dao = HouseDAO.getInstance();
        lock.writeLock().lock();
        try {
            count.addAndGet(dao.updateOrInsert(cache));
            LOG.info("save " + count.get() + " houses of " + sites);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void startWatchDaemon() {
        daemonLock.writeLock().lock();
        try {
            if (watchDaemon != null) {
                return;
            }
            watchDaemon = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        ThreadHelper.sleep(1000);
                        dumpCache();
                    }
                }
            };
            watchDaemon.setDaemon(true);
            watchDaemon.start();
        } finally {
            daemonLock.writeLock().unlock();
        }
    }

    private void dumpCache() {
        lock.readLock().lock();
        try {
            if (cache.size() < 500)
                return;
        } finally {
            lock.readLock().unlock();
        }

        List<House> houses;
        lock.writeLock().lock();
        try {
            houses = new ArrayList<>(cache);
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
        count.addAndGet(HouseDAO.getInstance().updateOrInsert(houses));
        LOG.trace("dump " + houses.size() + " houses");
    }
}
