package chookin.stock.extractor.gtimg;

import chookin.stock.orm.domain.RealDataEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by zhuyin on 3/21/15.
 */
public class RealDataPageProcessor implements PageProcessor {
    private final static Logger LOG = Logger.getLogger(RealDataPageProcessor.class);

    public static Request getRequest(StockEntity stock){
        return new Request(String.format("http://qt.gtimg.cn/q=%s%s", stock.getExchange(), stock.getCode()))
                .setPageProcessor(new RealDataPageProcessor());
    }

    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        StockEntity stock = page.getRequest().getExtra("stock", StockEntity.class);
        
        String strData = doc.text();
        int index = strData.indexOf("=");
        strData = strData.substring(index+1);
        String[] strArray = strData.split("\"");
        RealDataEntity data = new RealDataEntity();
        data.setStockCode(stock.getCode());
        if(strArray.length < 2){
            LOG.error(String.format("extract real data for stock %s, invalid data: %s", stock.getCode(), doc.text()));
        }
        strData = strArray[1];
        strArray = strData.split("~");
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");//小写的mm表示的是分钟
        try {
            Timestamp date = new Timestamp(sdf.parse(strArray[30]).getTime());
            data.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        try {
            data.setCurPrice(Double.parseDouble(strArray[3]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "curPrice"));
        }

        try {
            data.setYclose(Double.parseDouble(strArray[4]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "yClose"));
        }
        try {
            data.setOpen(Double.parseDouble(strArray[5]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "open"));
        }
        try {
            data.setPriceChange(Double.parseDouble(strArray[31]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "change"));
        }
        try {
            data.setChangeRatio(Double.parseDouble(strArray[32]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "changeRatio"));
        }
        try {
            data.setHighPrice(Double.parseDouble(strArray[33]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "highPrice"));
        }
        try {
            data.setLowPrice(Double.parseDouble(strArray[34]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "lowPrice"));
        }
        try {
            data.setMarketValue(Double.parseDouble(strArray[44]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "marcketCap"));
        }
        try {
            data.setTotalValue(Double.parseDouble(strArray[45]));
        }catch (java.lang.NumberFormatException e){
            LOG.error(String.format("extract real data for stock %s: invalid %s", stock.getCode(), "totalValue"));
        }
        page.setField("realData", data);
    }
}
