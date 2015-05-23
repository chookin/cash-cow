package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.HistoryDataEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.DateHelper;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by zhuyin on 5/20/15.
 */
public class HistDataPageProcessor implements PageProcessor{
    private static final Logger LOG = Logger.getLogger(HistDataPageProcessor.class);

    public static Request getRequest(StockEntity stock, int year, int quarter){
        String url = String.format("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%d&jidu=%d", stock.getCode(), year, quarter);
        return new Request(url)
                .putExtra("stock", stock)
                .putExtra("year", year)
                .setPageProcessor(new HistDataPageProcessor());
    }
    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        StockEntity stock = (StockEntity) page.getField("stock");
        int year = (int) page.getField("year");
        Elements elements = doc.select("table#FundHoldSharesTable");
        elements = elements.select("tr");
        for(Element element : elements){
            Elements rows = element.select("td");
            for(Iterator<Element> iter = rows.iterator();iter.hasNext();){
                Element row = iter.next();
                if(!row.text().startsWith(Integer.toString(year))){
                    break;
                }
                try{
                    HistoryDataEntity entity = new HistoryDataEntity();
                    entity.setStockCode(stock.getCode());
                    Date date = DateHelper.parseDate(row.text());
                    entity.setTime(date);
                    entity.setOpenPrice(Double.parseDouble(iter.next().text()));
                    entity.setHighPrice(Double.parseDouble(iter.next().text()));
                    entity.setClosePrice(Double.parseDouble(iter.next().text()));
                    entity.setLowPrice(Double.parseDouble(iter.next().text()));
                    entity.setTradeHand(Long.parseLong(iter.next().text()));
                    entity.setTradeValue(Long.parseLong(iter.next().text()));
                    page.setField("histData", entity);
                } catch (Throwable t) {
                    LOG.error(null, t);
                }
            }
        }
    }
}
