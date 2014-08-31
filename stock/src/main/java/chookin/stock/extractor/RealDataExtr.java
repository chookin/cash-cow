package chookin.stock.extractor;

import chookin.stock.orm.domain.RealDataEntity;
import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;

/**
 * Created by chookin on 7/6/14.
 */
public abstract class RealDataExtr {
    protected StockEntity stock;
    protected String url;
    public RealDataExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract RealDataEntity extract() throws IOException;
}
