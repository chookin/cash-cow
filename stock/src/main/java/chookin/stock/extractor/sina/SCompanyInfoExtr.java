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
    public void extract(CompanyInfoEntity entity) throws IOException {
        Extractor extractor = new Extractor(this.url, Configuration.getLocalResource().getLocalArchivePath());
        Document doc = extractor.getDocument(DateUtils.YEAR_MILLISECONDS);
        Elements elements = doc.select("table#comInfo1");
        elements = elements.select("td");
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            Element element = iter.next();
            String text = element.text();
            if(text.contains("公司名称")){
                String name = iter.next().text();
                if(name.isEmpty()){
                }else{
                    entity.setCompanyName(name);
                }
            }else if(text.contains("公司英文名称")){
                entity.setCompanyEnName(iter.next().text());
            }else if(text.contains("上市市场")){
                entity.setExchangeCenter(iter.next().text());
            }else if(text.contains("上市日期")){
                String strDate = iter.next().text();
                Date date = DateUtils.parseDate(strDate);
                entity.setListingDate(date);
            }else if(text.contains("发行价格")){
                String strValue = iter.next().text();
                if(strValue.length() > 0)
                    entity.setIssurePrice(Double.parseDouble(strValue));
            }else if(text.contains("主承销商")){
                entity.setLeadUnderWriter(iter.next().text());
            }else if(text.contains("成立日期")){
                String strDate = iter.next().text();
                Date date = DateUtils.parseDate(strDate);
                entity.setListingDate(date);
            }else if(text.contains("注册资本")){
                String strCapital = iter.next().text();
                double capital = 0;
                if(strCapital.length() > 2){
                    strCapital = strCapital.substring(0, strCapital.length() - 2);
                    capital = Double.parseDouble(strCapital);
                }
                entity.setRegisteredCapital(capital);
            }else if(text.contains("机构类型")){
                entity.setInsititutionType(iter.next().text());
            }else if(text.contains("组织形式")){
                entity.setOrganizationalForm(iter.next().text());
            }else if(text.contains("公司电话")){
                entity.setPhone(iter.next().text());
            }else if(text.contains("公司传真")){
                entity.setFax(iter.next().text());
            }else if(text.contains("公司电子邮箱")){
                entity.setEmail(iter.next().text());
            }else if(text.contains("公司网址")){
                String addr = iter.next().text();
                if(addr.isEmpty() || addr.equals("http://")){
                }else{
                    entity.setWebsite(iter.next().text());
                }
            }else if(text.contains("邮政编码")){
                entity.setZipcode(iter.next().text());
            }else if(text.contains("注册地址")){
                entity.setRegisteredAddress(iter.next().text());
            }else if(text.contains("办公地址")){
                entity.setOfficeAddress(iter.next().text());
            }else if(text.contains("公司简介")){
                entity.setCompanyProfile(iter.next().text());
            }else if(text.contains("经营范围")){
                entity.setBusinessScope(iter.next().text());
            }
        }
    }
}
