package chookin.stock.extractor.sina;

import chookin.etl.common.Extractor;
import chookin.stock.Configuration;
import chookin.stock.extractor.HistoryDataExtr;
import chookin.stock.orm.domain.HistoryDataEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.utils.DateUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chookin on 7/6/14.
 */
public class SHistoryDataExtr extends HistoryDataExtr {
    private final static Logger LOG = Logger.getLogger(SHistoryDataExtr.class);
    public SHistoryDataExtr(StockEntity stock){
        super(stock);
    }

    @Override
    public Collection<HistoryDataEntity> extract(int year, int quarter) throws IOException {
        String url = String.format("http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/%s.phtml?year=%d&jidu=%d",this.stock.getStockCode(), year, quarter);
        LOG.info("extract "+url);
        List<HistoryDataEntity> rst = new ArrayList<HistoryDataEntity>();
        Extractor extractor = new Extractor(url);
        Document doc = extractor.getDocument(0L);
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
                    entity.setStockId(this.stock.getStockId());
                    Date date = DateUtils.parseDate(row.text());
                    entity.setTime(date);
                    entity.setOpenPrice(Double.parseDouble(iter.next().text()));
                    entity.setHighPrice(Double.parseDouble(iter.next().text()));
                    entity.setClosePrice(Double.parseDouble(iter.next().text()));
                    entity.setLowPrice(Double.parseDouble(iter.next().text()));
                    entity.setTradeHand(Long.parseLong(iter.next().text()));
                    entity.setTradeValue(Long.parseLong(iter.next().text()));

                    rst.add(entity);
                } catch (Throwable t) {
                    LOG.error(null, t);
                }
            }

        }
        return rst;
    }

}
