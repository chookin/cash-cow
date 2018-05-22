package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.HistoryEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.TimeHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhuyin on 5/20/15.
 */
public class HistDataPageProcessor implements PageProcessor{
    private static final Logger LOG = LoggerFactory.getLogger(HistDataPageProcessor.class);
    static final HistDataPageProcessor processor = new HistDataPageProcessor();
    public static Request getRequest(StockEntity stock, int year, int quarter){
        String url = String.format("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%d&jidu=%d", stock.getCode(), year, quarter);
        return new Request(url, processor)
                .putExtra("stock", stock)
                .putExtra("year", year)
                ;
    }
    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        StockEntity stock = (StockEntity) page.getRequest().getExtra("stock");
        List<HistoryEntity> histData = new ArrayList<>();

        int year = (int) page.getRequest().getExtra("year");
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
                    HistoryEntity entity = new HistoryEntity();
                    entity.setStockCode(stock.getCode());
                    Date date = TimeHelper.parseDate(row.text());
                    entity.setDay(new java.sql.Date(date.getTime()));
                    entity.setOpenPrice(Double.parseDouble(iter.next().text()));
                    entity.setHighPrice(Double.parseDouble(iter.next().text()));
                    entity.setClosePrice(Double.parseDouble(iter.next().text()));
                    entity.setLowPrice(Double.parseDouble(iter.next().text()));
                    entity.setTradeHand(Long.parseLong(iter.next().text()));
                    entity.setTradeValue(Long.parseLong(iter.next().text()));
                    histData.add(entity);
                } catch (Throwable t) {
                    LOG.error(null, t);
                }
            }
        }
        page.setField("histData", histData);
    }
}
