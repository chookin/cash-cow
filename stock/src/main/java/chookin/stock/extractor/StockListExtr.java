package chookin.stock.extractor;

import chookin.stock.orm.domain.StockEntity;

import java.io.IOException;
import java.util.Map;

/**
 * Created by chookin on 7/6/14.
 */
public abstract class StockListExtr {
    protected String url;
    public StockListExtr setUrl(String url) {this.url = url; return this;}
    public String getUrl() {return this.url;}

    public abstract Map<String, StockEntity> getNewStocks(Map<String, StockEntity> existedStocks) throws IOException;
}
