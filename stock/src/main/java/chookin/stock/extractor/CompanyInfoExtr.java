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

    /**
     * extract company info and update entity
     * @param entity the stock company info entity that needed update.
     * @throws IOException
     */
    public abstract void extract(CompanyInfoEntity entity) throws IOException;
}
