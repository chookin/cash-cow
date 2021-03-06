package chookin.stock.extractor.eastmoney;

import chookin.stock.orm.domain.Exchange;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by zhuyin on 3/22/15.
 * Retrieve stock infos from the web page
 */
public class StockPageProcessor implements PageProcessor {
    static final StockPageProcessor processor = new StockPageProcessor();
    public static Request getRequest(Map<String, StockEntity> existedStocks){
        return new Request("http://quote.eastmoney.com/stocklist.html", processor)
                .putExtra("existedStocks", existedStocks);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        Map<String, StockEntity> existedStocks = page.getRequest().getExtra("existedStocks", Map.class);
        Map<String, StockEntity> stocks = new TreeMap<>();

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
                    String stockCode = array[1];
                    if(!existedStocks.containsKey(stockCode)){
                        // 如果已存储，就不再处理
                        StockEntity stock = new StockEntity();
                        String stockName = array[0];
                        stock.setExchange(exchange.toString());
                        stock.setName(stockName);
                        stock.setCode(stockCode);
                        stocks.put(stock.getCode(), stock);
                    }
                }
            }
        }
        page.setField("stockCollection", stocks);
    }
}
