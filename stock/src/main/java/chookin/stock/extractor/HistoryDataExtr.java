package chookin.stock.extractor;

import chookin.stock.Data.HistoryData;
import chookin.stock.orm.domain.HistoryDataEntity;
import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by chookin on 7/6/14.
 */
public abstract class HistoryDataExtr {
    protected String url;
    protected StockEntity stock;
    public HistoryDataExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract Collection<HistoryDataEntity> extract(int year, int quarter) throws IOException;
}
