package chookin.stock.extractor.sina;

import chookin.stock.extractor.HistoryDataDetailExtr;
import chookin.stock.orm.domain.HistoryDataDetailEntity;
import chookin.stock.orm.domain.StockEntity;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chookin on 8/2/14.
 */
public class SHistoryDataDetailExtr extends HistoryDataDetailExtr {
    private final static Logger LOG = Logger.getLogger(SHistoryDataExtr.class);
    public SHistoryDataDetailExtr(StockEntity stock){
        super(stock);
    }
    @Override
    public Collection<HistoryDataDetailEntity> extract(Date date) throws IOException {
        // String url = String.format("http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=%s%s&date=%s",this.stock.getStockCode(), this.stock.getExchange(), date.toString());
        String url = String.format("http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s", date.toString(), this.stock.getExchange(), this.stock.getStockCode());
        LOG.info("extract "+url);
        List<HistoryDataDetailEntity> rst = new ArrayList<HistoryDataDetailEntity>();

        return rst;
    }
}
