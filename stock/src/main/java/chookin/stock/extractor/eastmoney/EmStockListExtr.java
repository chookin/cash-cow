package chookin.stock.extractor.eastmoney;

import chookin.etl.common.Extractor;
import chookin.stock.Data.Exchange;
import chookin.stock.extractor.StockListExtr;
import chookin.stock.orm.domain.StockEntity;
import chookin.utils.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chookin on 7/6/14.
 */
public class EmStockListExtr extends StockListExtr {
    public EmStockListExtr(){
        this.url = "http://quote.eastmoney.com/stocklist.html";
    }

    /**
     * retrieve stock infos from the web page
     *
     * @return
     * @throws IOException
     */
    @Override
    public Map<String, StockEntity> getStocks() throws IOException{
        Map<String, StockEntity> stocks = new TreeMap<String, StockEntity>();
        Document doc = new Extractor(this.getUrl()).getDocument(DateUtils.MONTH_MILLISECONDS);
        Elements elements = doc.select("div.qox");
        elements = elements.select(".sltit, li");
        Exchange exchange = Exchange.Unknown;
        for(Element item : elements){
            String text = item.text();
            if(item.select("div").size() > 0){
                // <div class="sltit"><a name="sh"/>上海股票</div>
                if(text.contains("上海")){
                    exchange = Exchange.ShangHai;
                }else if(text.contains("深圳")){
                    exchange = Exchange.ShenZhen;
                }
            }else{
                // <li><a target="_blank" href="http://quote.eastmoney.com/sh201000.html">R003(201000)</a></li>
                String delimiter = "\\(|\\)";
                String[] array = text.split(delimiter);
                if(array.length == 2){
                    StockEntity stock = new StockEntity();
                    stock.setExchange(exchange.toString());
                    stock.setStockName(array[0]);
                    stock.setStockCode(array[1]);
                    stocks.put(stock.getStockCode(), stock);
                }
            }
        }
        return stocks;
    }
}
