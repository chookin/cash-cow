package chookin.stock.extractor.sina;

import chookin.etl.common.Extractor;
import chookin.stock.extractor.HistoryDataDetailExtr;
import chookin.stock.orm.domain.DayExchangeEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.utils.DateUtils;
import chookin.utils.io.FileHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
    public boolean extract(Date date) throws IOException {
        String url = String.format("http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s%s", DateUtils.convertToDateString(date), this.stock.getExchange(),this.stock.getStockCode());
        LOG.info("extract "+url);
        Extractor extractor = new MyExtractor(url);
        return extractor.saveAsResource(100);
    }
    static class MyExtractor extends Extractor{
        /**
         *
         * @param url such as http://market.finance.sina.com.cn/downxls.php?date=2015-01-27&symbol=sz000001
         */
        public MyExtractor(String url) {
            super(url, Long.MAX_VALUE);
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

    static class ResourceFile{
        /**
         the source file format such as:
         成交时间	成交价	价格变动	成交量(手)	成交额(元)	性质
         15:00:20	10.61	--	5250	5570419	买盘
         14:57:02	10.61	--	10	10610	卖盘
         14:56:59	10.61	0.01	107	113569	买盘

         * @param fileName the name of history detail csv file. file name format:/home/chookin/stock/market.finance.sina.com.cn/2014-11-18/sz000001.dat
         * @return the retrieved entities.
         */
        public List<DayExchangeEntity> extractFile(String fileName) throws IOException {
            List<DayExchangeEntity> entities = new ArrayList<>();
            String absFileName = new File(fileName).getAbsolutePath();
            int indexLastSlash = absFileName.lastIndexOf("/");
            int indexNextLastSlash = absFileName.lastIndexOf("/", indexLastSlash);
            int indexLastDot = absFileName.lastIndexOf(".");
            if (indexLastSlash == -1 || indexNextLastSlash == -1){
                throw new IOException("invalid hist detail file: " + absFileName);
            }
            String stock_code = absFileName.substring(indexLastSlash + 3, indexLastDot); // remove the sz or sh
            String strdate = absFileName.substring(indexNextLastSlash + 1, indexLastSlash);
            Date date = DateUtils.parseDate(strdate);

            List<String> lines = FileHelper.readLines(fileName, "gbk");
            int count = -1;
            for(String line: lines){
                        count += 1;
                if( count == 0 ) {
                    continue; // ignore the head line
                }
                String[] items = line.split("\t");
                // TODO
            }
            return entities;
        }
    }

}
