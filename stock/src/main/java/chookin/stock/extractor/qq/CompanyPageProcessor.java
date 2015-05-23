package chookin.stock.extractor.qq;

import chookin.stock.orm.domain.CompanyInfoEntity;
import chookin.stock.orm.domain.StockEntity;
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
    private final static String INVALID_NUM ="--";

    /**
     * http://stockhtm.finance.qq.com/sstock/ggcx/600030.shtml
     * @return
     */
    public static String getUrl(StockEntity stock){
        return String.format("http://stockhtm.finance.qq.com/sstock/ggcx/%s.shtml",stock.getStockCode());
    }
    @Override
    public void process(ResultItems page) {
        extractProfits(page);
        extractSpot(page);
    }
    private void extractSpot(ResultItems page){
        Document doc = (Document) page.getResource();
        CompanyInfoEntity company = page.getRequest().getExtra("company", CompanyInfoEntity.class);
        Elements spotElements = doc.select("div#mod-tzld > table#table_tzld");
        spotElements = spotElements.select("th,td");
        StringBuilder strb = new StringBuilder();
        for(Iterator<Element> iter = spotElements.iterator();iter.hasNext();){
            Element element = iter.next();
            String textNum = element.text();
            String value = iter.next().text();
            strb.append(textNum).append(value).append("\n\n");
        }
        company.setInvestSpot(strb.toString());
    }
    private void extractProfits(ResultItems page){
        Document doc = (Document) page.getResource();
        CompanyInfoEntity company = page.getRequest().getExtra("company", CompanyInfoEntity.class);

        Elements elements = doc.select("div#mod-gsgk > table.data");
        elements = elements.select("td,th");//all matching elements td, th
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            Element element = iter.next();
            String text = element.text();
            if(text.contains("总股本")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setStockNum(Double.parseDouble(value));
            }else if(text.contains("每股收益")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setEps(Double.parseDouble(value));
            }else if(text.contains("流通A股")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setTradable(Double.parseDouble(value));
            }else if(text.contains("每股净资产")){
                String value = iter.next().text();
                if(isInvalid(value)) {
                    continue;
                }
                company.setNetAsset(Double.parseDouble(value));
            }else if(text.contains("每股现金流")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setCashFlow(Double.parseDouble(value));
            }else if(text.contains("每股公积金")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setFund(Double.parseDouble(value));
            }else if(text.contains("净资产收益率")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setEquity(Double.parseDouble(value));
            }else if(text.contains("净利润增长率")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setGrowth(Double.parseDouble(value));
            }else if(text.contains("每股未分配利润")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setProfit(Double.parseDouble(value));
            }else if(text.contains("主营收入增长率")){
                String value = iter.next().text();
                if(isInvalid(value)){
                    continue;
                }
                company.setGross(Double.parseDouble(value));
            }
        }
    }

    private boolean isInvalid(String value){
        return INVALID_NUM.equals(value);
    }
}
