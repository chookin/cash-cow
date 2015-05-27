package chookin.stock.handler;

import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.StockRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class StockMapHandler {
    private final static Logger LOG = Logger.getLogger(StockMapHandler.class);
    @Autowired
    private StockRepository stockRepository;
    private Map<String, StockEntity> stocksMap = new ConcurrentSkipListMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private static StockMapHandler handler;

    @PostConstruct
    private void init(){
        handler = this;
    }

    private void reLoad(){
        lock.writeLock().lock();
        try {
            stocksMap.clear();
            Iterable<StockEntity> stocks = stockRepository.findAll();
            Map<String, StockEntity> map = new TreeMap<>();
            for(StockEntity item : stocks){
                if(!item.getDiscard()){
                    map.put(item.getCode(), item);
                }
            }
            stocksMap.putAll(map);
            LOG.info(String.format("get %d stocks", stocksMap.size()));
        }finally {
            lock.writeLock().unlock();
        }
    }

    private Map<String, StockEntity> _getStocksMap(){
        lock.readLock().lock();
        try{
            if(!stocksMap.isEmpty()){
                return new HashMap<>(stocksMap);
            }
        }finally {
            lock.readLock().unlock();
        }

        reLoad();
        lock.readLock().lock();
        try {
            return new HashMap<>(stocksMap);
        }finally {
            lock.readLock().unlock();
        }
    }

    /**
     * @return copy of stock's map
     */
    public static Map<String, StockEntity> getStocksMap(){
        return handler._getStocksMap();
    }
}
