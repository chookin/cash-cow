package chookin.stock.extractor.eastmoney;

import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * Created by zhuyin on 3/21/15.
 */
public class CompanyPageProcessor implements PageProcessor {

    /**
     * http://f10.eastmoney.com/f10_v2/CoreConception.aspx?code=sh600406
     */
    public static String getUrl(StockEntity stock){
        return String.format("http://f10.eastmoney.com/f10_v2/CoreConception.aspx?code=%s%s",stock.getExchange(), stock.getCode());
    }

    public static Request getRequest(StockEntity stock){
        return new Request()
                .setPageProcessor(new CompanyPageProcessor())
                .setUrl(getUrl(stock));
    }
    @Override
    public void process(ResultItems page) {
        extractCoreTheme(page);
    }
    private void extractCoreTheme(ResultItems page){
        Document doc = (Document) page.getResource();
        CompanyInfoEntity company = page.getRequest().getExtra("company", CompanyInfoEntity.class);

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
                    company.setTags(market);
                }
            }
        }
        company.setCoreTheme(coreTheme.toString());
    }
}
