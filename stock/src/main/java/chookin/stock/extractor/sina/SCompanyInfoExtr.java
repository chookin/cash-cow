package chookin.stock.extractor.sina;

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
import java.sql.Date;
import java.util.Iterator;

/**
 * Created by chookin on 7/6/14.
 * http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/600030.phtml
 */
public class SCompanyInfoExtr extends CompanyInfoExtr {
    private final static Logger LOG = Logger.getLogger(SCompanyInfoExtr.class);
    public SCompanyInfoExtr(StockEntity stock){
        super(stock);
        this.url = String.format("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/%s.phtml",this.stock.getStockCode());
    }
    @Override
    public CompanyInfoEntity extract() throws IOException {
        Extractor extractor = new Extractor(this.url, Configuration.getLocalResource().getLocalArchivePath());
        Document doc = extractor.getDocument(DateUtils.YEAR_MILLISECONDS);
        extractor.save(doc);
        Elements elements = doc.select("table#comInfo1");
        elements = elements.select("td");
        CompanyInfoEntity company = new CompanyInfoEntity();
        company.setStockCode(this.stock.getStockCode());
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            Element element = iter.next();
            String text = element.text();
            if(text.contains("公司名称")){
                String name = iter.next().text();
                if(name.isEmpty()){
                    return null;
                }else{
                    company.setCompanyName(name);
                }
            }else if(text.contains("公司英文名称")){
                company.setCompanyEnName(iter.next().text());
            }else if(text.contains("上市市场")){
                company.setExchangeCenter(iter.next().text());
            }else if(text.contains("上市日期")){
                String strDate = iter.next().text();
                Date date = DateUtils.parseDate(strDate);
                company.setListingDate(date);
            }else if(text.contains("发行价格")){
                String strValue = iter.next().text();
                if(strValue.length() > 0)
                    company.setIssurePrice(Double.parseDouble(strValue));
            }else if(text.contains("主承销商")){
                company.setLeadUnderWriter(iter.next().text());
            }else if(text.contains("成立日期")){
                String strDate = iter.next().text();
                Date date = DateUtils.parseDate(strDate);
                company.setListingDate(date);
            }else if(text.contains("注册资本")){
                String strCapital = iter.next().text();
                double capital = 0;
                if(strCapital.length() > 2){
                    strCapital = strCapital.substring(0, strCapital.length() - 2);
                    capital = Double.parseDouble(strCapital);
                }
                company.setRegisteredCapital(capital);
            }else if(text.contains("机构类型")){
                company.setInsititutionType(iter.next().text());
            }else if(text.contains("组织形式")){
                company.setOrganizationalForm(iter.next().text());
            }else if(text.contains("公司电话")){
                company.setPhone(iter.next().text());
            }else if(text.contains("公司传真")){
                company.setFax(iter.next().text());
            }else if(text.contains("公司电子邮箱")){
                company.setEmail(iter.next().text());
            }else if(text.contains("公司网址")){
                String addr = iter.next().text();
                if(addr.isEmpty() || addr.equals("http://")){
                }else{
                    company.setWebsite(iter.next().text());
                }
            }else if(text.contains("邮政编码")){
                company.setZipcode(iter.next().text());
            }else if(text.contains("注册地址")){
                company.setRegisteredAddress(iter.next().text());
            }else if(text.contains("办公地址")){
                company.setOfficeAddress(iter.next().text());
            }else if(text.contains("公司简介")){
                company.setCompanyProfile(iter.next().text());
            }else if(text.contains("经营范围")){
                company.setBusinessScope(iter.next().text());
            }
        }
        return company;
    }
}
