package chookin.stock.Data;

import java.util.Date;

/**
 * http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_CorpInfo/stockid/600030.phtml
 * Created by chookin on 7/6/14.
 */
public class CompayInfo {
    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyEnName() {
        return companyEnName;
    }

    public void setCompanyEnName(String companyEnName) {
        this.companyEnName = companyEnName;
    }

    public String getExchangeCenter() {
        return exchangeCenter;
    }

    public void setExchangeCenter(String exchangeCenter) {
        this.exchangeCenter = exchangeCenter;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public double getIssurePrice() {
        return issurePrice;
    }

    public void setIssurePrice(double issurePrice) {
        this.issurePrice = issurePrice;
    }

    public String getLeadUnderwriter() {
        return leadUnderwriter;
    }

    public void setLeadUnderwriter(String leadUnderwriter) {
        this.leadUnderwriter = leadUnderwriter;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public double getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(double registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getInstitutionalType() {
        return institutionalType;
    }

    public void setInstitutionalType(String institutionalType) {
        this.institutionalType = institutionalType;
    }

    public String getOrganizationalForm() {
        return organizationalForm;
    }

    public void setOrganizationalForm(String organizationalForm) {
        this.organizationalForm = organizationalForm;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
    }

    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    private String stockId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 公司英文名称
     */
    private String companyEnName;
    /**
     * 上市市场
     */
    private String exchangeCenter;
    /**
     * 上市日期
     */
    private Date listingDate;
    /**
     * 发行价格
     */
    private double issurePrice;
    /**
     * 主承销商
     */
    private String leadUnderwriter;
    /**
     * 成立日期
     */
    private Date registrationDate;
    /**
     * 注册资本
     */
    private double registeredCapital;
    /**
     * 机构类型
     */
    private String institutionalType;
    /**
     * 组织形式
     */
    private String organizationalForm;
    /**
     * 公司电话
     */
    private String phone;
    /**
     * 公司传真
     */
    private String fax;
    /**
     * 公司电子邮箱
     */
    private String email;
    /**
     * 公司网址
     */
    private String website;
    /**
     * 邮政编码
     */
    private String zipcode;
    /**
     * 注册地址
     */
    private String registeredAddress;
    /**
     * 办公地址
     */
    private String officeAddress;
    /**
     * 公司简介
     */
    private String companyProfile;
    /**
     * 经营范围
     */
    private String businessScope;
}
