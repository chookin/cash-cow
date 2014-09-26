package chookin.stock.extractor;

import chookin.stock.orm.domain.HistoryDataDetailEntity;
import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;
import java.util.Date;
import java.util.Collection;

/**
 * Created by chookin on 8/2/14.
 * http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz000028&date=2014-07-03
 */
public abstract class HistoryDataDetailExtr {
    protected StockEntity stock;
    public HistoryDataDetailExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract void extract(Date date) throws IOException;
}
