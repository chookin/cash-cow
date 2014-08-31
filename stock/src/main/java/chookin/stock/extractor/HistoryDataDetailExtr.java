package chookin.stock.extractor;

import chookin.stock.orm.domain.HistoryDataDetailEntity;
import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;
import java.sql.Date;
import java.util.Collection;

/**
 * Created by chookin on 8/2/14.
 */
public abstract class HistoryDataDetailExtr {
    protected String url;
    protected StockEntity stock;
    public HistoryDataDetailExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract Collection<HistoryDataDetailEntity> extract(Date date) throws IOException;
}
