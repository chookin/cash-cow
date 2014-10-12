package chookin.stock.extractor.sina;

import chookin.etl.common.Extractor;
import chookin.stock.extractor.HistoryDataDetailExtr;
import chookin.stock.orm.domain.HistoryDataDetailEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.utils.DateUtils;
import chookin.utils.io.FileHelper;
import chookin.utils.web.UrlHelper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chookin on 8/2/14.
 * http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz000028&date=2014-07-03
 */
public class SHistoryDataDetailExtr extends HistoryDataDetailExtr {
    private final static Logger LOG = Logger.getLogger(SHistoryDataExtr.class);
    public SHistoryDataDetailExtr(StockEntity stock){
        super(stock);

    }
    @Override
    public void extract(Date date) throws IOException {
        String url = String.format("http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s%s", DateUtils.convertToDateString(date), this.stock.getExchange(),this.stock.getStockCode());
        LOG.info("extract "+url);
        Extractor extractor = new MyExtractor(url);
        extractor.saveAsResource(100);
    }
    class MyExtractor extends Extractor{

        public MyExtractor(String url) {
            super(url);
        }
        @Override
        public String getFileName(){
            if(this.localFileName != null){
                return this.localFileName;
            }
            String fileName = super.getFileName();
            int indexLastSlash = fileName.lastIndexOf("/");
            String dir = fileName.substring(0, indexLastSlash);
            String dateMark = "date=";
            String symbolMark = "-symbol=";
            int indexdate = fileName.lastIndexOf(dateMark);
            int indexSymbol = fileName.lastIndexOf(symbolMark);
            String strDate = fileName.substring(indexdate + dateMark.length(), indexSymbol);
            String stock = fileName.substring(indexSymbol + symbolMark.length());
            this.localFileName = String.format("%s/%s/%s.dat", dir, strDate, stock);
            return this.localFileName;
        }
    }

}
