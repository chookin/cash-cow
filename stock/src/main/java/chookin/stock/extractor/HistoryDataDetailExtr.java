package chookin.stock.extractor;

import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;
import java.util.Date;

/**
 * Created by chookin on 8/2/14.
 */
public abstract class HistoryDataDetailExtr {
    protected StockEntity stock;
    public HistoryDataDetailExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract void extract(Date date) throws IOException;
}
