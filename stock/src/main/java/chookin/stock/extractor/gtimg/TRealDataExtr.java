package chookin.stock.extractor.gtimg;

import chookin.etl.common.Extractor;
import chookin.stock.extractor.RealDataExtr;
import chookin.stock.orm.domain.RealDataEntity;
import chookin.stock.orm.domain.StockEntity;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * http://qt.gtimg.cn/q=sz000858
 * Created by chookin on 7/6/14.
 */
public class TRealDataExtr extends RealDataExtr{
    private final static Logger LOG = Logger.getLogger(TRealDataExtr.class);
    public TRealDataExtr(StockEntity stock){
        super(stock);
        this.url = String.format("http://qt.gtimg.cn/q=%s%s", this.stock.getExchange(), this.stock.getStockCode());
    }

    @Override
    public RealDataEntity extract() throws IOException {
        Document doc = new Extractor(this.url).getDocument();
        String strData = doc.text();
        int index = strData.indexOf("=");
        strData = strData.substring(index+1);
        String[] strArray = strData.split("\"");
        RealDataEntity data = new RealDataEntity();
        data.setStockCode(this.stock.getStockCode());
        if(strArray.length < 2){
            LOG.error(String.format("extract real data for stock %s, invalid data: %s", this.stock.getStockCode(), doc.text()));
            return  data;
        }
        strData = strArray[1];
        strArray = strData.split("~");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");//小写的mm表示的是分钟
        try {
            Timestamp date = new Timestamp(sdf.parse(strArray[30]).getTime());
            data.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return data;
        }
        try {
            data.setCurPrice(Double.parseDouble(strArray[3]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "curPrice"));
        }

        try {
            data.setYclose(Double.parseDouble(strArray[4]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "yClose"));
        }
        try {
            data.setOpen(Double.parseDouble(strArray[5]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "open"));
        }
        try {
            data.setPriceChange(Double.parseDouble(strArray[31]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "change"));
        }
        try {
            data.setChangeRatio(Double.parseDouble(strArray[32]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "changeRatio"));
        }
        try {
            data.setHighPrice(Double.parseDouble(strArray[33]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "highPrice"));
        }
        try {
            data.setLowPrice(Double.parseDouble(strArray[34]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "lowPrice"));
        }
        try {
            data.setMarketValue(Double.parseDouble(strArray[44]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "marcketCap"));
        }
        try {
            data.setTotalValue(Double.parseDouble(strArray[45]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", this.stock.getStockCode(), "totalValue"));
        }
        return  data;
    }
}
