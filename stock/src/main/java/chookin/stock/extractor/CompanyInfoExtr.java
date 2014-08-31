package chookin.stock.extractor;

import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;

/**
 * Created by chookin on 7/6/14.
 */
public abstract class CompanyInfoExtr {
    protected StockEntity stock;
    protected String url;
    public CompanyInfoExtr(StockEntity stock){
        this.stock = stock;
    }
    public abstract CompanyInfoEntity extract() throws IOException;
}
