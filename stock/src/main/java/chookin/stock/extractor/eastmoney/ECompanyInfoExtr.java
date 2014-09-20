package chookin.stock.extractor.eastmoney;

import chookin.etl.common.Extractor;
import chookin.stock.Configuration;
import chookin.stock.extractor.CompanyInfoExtr;
import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;
import chookin.utils.DateUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by chookin on 9/20/14.
 * http://f10.eastmoney.com/f10_v2/CoreConception.aspx?code=sh600406
 */
public class ECompanyInfoExtr extends CompanyInfoExtr {
    private final static Logger LOG = Logger.getLogger(ECompanyInfoExtr.class);
    public ECompanyInfoExtr(StockEntity stock){
        super(stock);
        this.url = String.format("http://f10.eastmoney.com/f10_v2/CoreConception.aspx?code=%s%s",this.stock.getExchange(), this.stock.getStockCode());
    }
    @Override
    public void extract(CompanyInfoEntity entity) throws IOException {
        Extractor extractor = new Extractor(this.url);
        Document doc = extractor.getDocument(DateUtils.YEAR_MILLISECONDS);
        extractCoreTheme(doc, entity);
    }
    private void extractCoreTheme(Document doc, CompanyInfoEntity entity){
        Elements elements = doc.select("div.summary").select("p");
        StringBuilder coreTheme = new StringBuilder();
        int indexMarket = -1;
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            String text = iter.next().text();
            coreTheme.append(text).append("\n\n");

            if(indexMarket == -1){
                indexMarket = text.indexOf("所属板块");
                if(indexMarket != -1){
                    int start = indexMarket + 4 + 1;
                    String market = text.substring(start);
                    entity.setTags(market);
                }
            }
        }
        entity.setCoreTheme(coreTheme.toString());
    }
}
