package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.CompanyInfoEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.DateHelper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;
import java.util.Iterator;

/**
 * Created by zhuyin on 3/21/15.
 */
public class CompanyPageProcessor implements PageProcessor {

    /**
     * http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/600030.phtml
     */
    public static String getUrl(CompanyInfoEntity entity){
        return String.format("http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/%s.phtml", entity.getStockCode());
    }

    public static Request getRequest(CompanyInfoEntity entity){
        return new Request()
                .setPageProcessor(new CompanyPageProcessor())
                .setUrl(getUrl(entity))
                .putExtra("company", entity);
    }
    @Override
    public void process(ResultItems page) {
        Document doc = (Document) page.getResource();
        CompanyInfoEntity company = page.getRequest().getExtra("company", CompanyInfoEntity.class);

        Elements elements = doc.select("table#comInfo1");
        elements = elements.select("td");
        for(Iterator<Element> iter = elements.iterator();iter.hasNext();){
            Element element = iter.next();
            String text = element.text();
            if(text.contains("公司名称")){
                String name = iter.next().text();
                if(name.isEmpty()){
                    page.skip(true); // 如果公司名称为空，则跳过处理
                    return;
                }else{
                    company.setCompanyName(name);
                }
            }else if(text.contains("公司英文名称")){
                company.setCompanyEnName(iter.next().text());
            }else if(text.contains("上市市场")){
                company.setExchangeCenter(iter.next().text());
            }else if(text.contains("上市日期")){
                String strDate = iter.next().text();
                if (!strDate.equals("--")) {
                    Date date = DateHelper.parseDate(strDate);
                    company.setListingDate(date);
                }
            }else if(text.contains("发行价格")){
                String strValue = iter.next().text();
                if(strValue.length() > 0)
                    company.setIssuePrice(Double.parseDouble(strValue));
            }else if(text.contains("主承销商")){
                company.setLeadUnderWriter(iter.next().text());
            }else if(text.contains("成立日期")){
                String strDate = iter.next().text();
                if(!strDate.isEmpty()) {
                    Date date = DateHelper.parseDate(strDate);
                    company.setRegistrationDate(date);
                }
            }else if(text.contains("注册资本")){
                String strCapital = iter.next().text();
                double capital = 0;
                if(strCapital.length() > 2){
                    strCapital = strCapital.substring(0, strCapital.length() - 2);
                    capital = Double.parseDouble(strCapital);
                }
                company.setRegisteredCapital(capital);
            }else if(text.contains("机构类型")){
                company.setInstitutionType(iter.next().text());
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
                    company.setWebsite(addr);
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
    }
}
