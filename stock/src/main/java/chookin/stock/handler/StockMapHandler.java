package chookin.stock.handler;

import chookin.stock.orm.domain.StockEntity;
import chookin.stock.orm.repository.StockRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by zhuyin on 3/21/15.
 */
@Service
public class StockMapHandler {
    private final static Logger LOG = Logger.getLogger(StockMapHandler.class);
    private Map<String, StockEntity> stocksMap = new ConcurrentSkipListMap<>();
    @Autowired
    private StockRepository stockRepository;

    private static StockMapHandler handler;

    @PostConstruct
    private void init(){
        handler = this;
        Iterable<StockEntity> stocks = stockRepository.findAll();
        Map<String, StockEntity> map = new TreeMap<>();
        for(StockEntity item : stocks){
            if(!item.getDiscard()){
                map.put(item.getCode(), item);
            }
        }
        stocksMap.putAll(map);
        LOG.info(String.format("get %d stocks", stocksMap.size()));
    }
    public static Map<String, StockEntity> getStocksMap(){
        return handler.stocksMap;
    }
}
