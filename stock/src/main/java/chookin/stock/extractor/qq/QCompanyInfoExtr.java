package chookin.stock.extractor.qq;

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
 * Created by chookin on 9/18/14.
 * http://stockhtm.finance.qq.com/sstock/ggcx/600030.shtml
 */
public class QCompanyInfoExtr extends CompanyInfoExtr {
    private final static Logger LOG = Logger.getLogger(QCompanyInfoExtr.class);
    private final static String INVALID_NUM ="--";
    public QCompanyInfoExtr(StockEntity stock){
        super(stock);
        this.url = String.format("http://stockhtm.finance.qq.com/sstock/ggcx/%s.shtml",this.stock.getStockCode());
    }
    @Override
    public CompanyInfoEntity extract() throws IOException {
        Extractor extractor = new Extractor(this.url, Configuration.getLocalResource().getLocalArchivePath());
        Document doc = extractor.getDocument(DateUtils.YEAR_MILLISECONDS);
        extractor.save(doc);
        CompanyInfoEntity company = new CompanyInfoEntity();
        extractProfits(doc, company);
        extractSpot(doc, company);
        return company;
    }
    private void extractSpot(Document doc,CompanyInfoEntity company){
        Elements spotElements = doc.select("table#table_tzld");
        spotElements = spotElements.select("th,td");
        company.setStockCode(this.stock.getStockCode());
        StringBuilder strb = new StringBuilder();
        for(Iterator<Element> iter = spotElements.iterator();iter.hasNext();){
            Element element = iter.next();
            String textNum = element.text();
            String value = iter.next().text();
            strb.append(textNum).append(value).append("\n");
        }
        company.setInvestSpot(strb.toString());
    }
    private void extractProfits(Document doc, CompanyInfoEntity company){
        Elements elements = doc.select("table.data");
        elements = elements.select("td,th");//all matching elements td, th
        company.setStockCode(this.stock.getStockCode());
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            Element element = iter.next();
            String text = element.text();
            if(text.contains("总股本")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setStockNum(Double.parseDouble(value));
            }else if(text.contains("每股收益")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setEps(Double.parseDouble(value));
            }else if(text.contains("流通A股")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setTradable(Double.parseDouble(value));
            }else if(text.contains("每股净资产")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setNetAsset(Double.parseDouble(value));
            }else if(text.contains("每股现金流")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setCashFlow(Double.parseDouble(value));
            }else if(text.contains("每股公积金")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setFund(Double.parseDouble(value));
            }else if(text.contains("净资产收益率")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setEquity(Double.parseDouble(value));
            }else if(text.contains("净利润增长率")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setGrowth(Double.parseDouble(value));
            }else if(text.contains("每股未分配利润")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setProfit(Double.parseDouble(value));
            }else if(text.contains("主营收入增长率")){
                String value = iter.next().text();
                if(value == INVALID_NUM){
                    continue;
                }
                company.setGross(Double.parseDouble(value));
            }
        }
    }
}