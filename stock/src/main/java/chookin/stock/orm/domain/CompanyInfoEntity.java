package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@javax.persistence.Table(name = "company_info", schema = "", catalog = "stock")
public class CompanyInfoEntity {

    private String stockCode;

    @Id
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    private String companyName;

    @Basic
    @Column
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    private String companyEnName;

    @Basic
    @Column
    public String getCompanyEnName() {
        return companyEnName;
    }

    public void setCompanyEnName(String companyEnName) {
        this.companyEnName = companyEnName;
    }

    private String exchangeCenter;

    @Basic
    @Column
    public String getExchangeCenter() {
        return exchangeCenter;
    }

    public void setExchangeCenter(String exchangeCenter) {
        this.exchangeCenter = exchangeCenter;
    }

    private Date listingDate;

    @Basic
    @Column
    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }
    public void setListingDate(java.util.Date listingDate) {
        this.listingDate = new Date(listingDate.getTime());
    }

    private Double issurePrice;

    @Basic
    @Column
    public Double getIssurePrice() {
        return issurePrice;
    }

    public void setIssurePrice(Double issurePrice) {
        this.issurePrice = issurePrice;
    }

    private String leadUnderWriter;

    @Basic
    @Column
    public String getLeadUnderWriter() {
        return leadUnderWriter;
    }

    public void setLeadUnderWriter(String leadUnderWriter) {
        this.leadUnderWriter = leadUnderWriter;
    }

    private Date registrationDate;

    @Basic
    @Column
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    public void setRegistrationDate(java.util.Date registrationDate) {
        this.registrationDate = new Date(registrationDate.getTime());
    }
    private Double registeredCapital;

    @Basic
    @Column
    public Double getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(Double registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    private String insititutionType;

    @Basic
    @Column
    public String getInsititutionType() {
        return insititutionType;
    }

    public void setInsititutionType(String insititutionType) {
        this.insititutionType = insititutionType;
    }

    private String organizationalForm;

    @Basic
    @Column
    public String getOrganizationalForm() {
        return organizationalForm;
    }

    public void setOrganizationalForm(String organizationalForm) {
        this.organizationalForm = organizationalForm;
    }

    private String phone;

    @Basic
    @Column
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String fax;

    @Basic
    @Column
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    private String email;

    @Basic
    @Column
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String website;

    @Basic
    @Column
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String zipcode;

    @Basic
    @Column
    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    private String registeredAddress;

    @Basic
    @Column
    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    private String officeAddress;

    @Basic
    @Column
    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    private String companyProfile;

    @Basic
    @Column
    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
    }

    private String businessScope;

    @Basic
    @Column
    public String getBusinessScope() {
        return businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    private String tags;
    @Basic
    public String getTags(){return tags;}
    public void setTags(String tags){this.tags = tags;}

    private Double stockNum;
    @Basic
    public Double getStockNum(){return stockNum;}
    public void setStockNum(Double stockNum){this.stockNum = stockNum;}

    private Double tradable;
    @Basic
    public Double getTradable(){return tradable;}
    public void setTradable(Double tradable){this.tradable = tradable;}

    private Double eps;
    @Basic
    public Double getEps(){return eps;}
    public void setEps(Double eps){this.eps = eps;}

    private Double netAsset;
    @Basic
    public Double getNetAsset(){return netAsset;}
    public void setNetAsset(Double netAsset){this.netAsset = netAsset;}

    private Double cashFlow;
    @Basic
    public Double getCashFlow(){return cashFlow;}
    public void setCashFlow(Double cashFlow){this.cashFlow = cashFlow;}

    private Double fund;
    @Basic
    public Double getFund(){return fund;}
    public void setFund(Double fund){this.fund = fund;}

    private Double profit;
    @Basic
    public Double getProfit(){return profit;}
    public void setProfit(Double profit){this.profit = profit;}

    private Double equity;
    @Basic
    public Double getEquity(){return equity;}
    public void setEquity(Double equity){this.equity = equity;}

    private Double growth;
    @Basic
    public Double getGrowth(){return growth;}
    public void setGrowth(Double growth){this.growth = growth;}

    private Double gross;
    @Basic
    public Double getGross(){return gross;}
    public void setGross(Double gross){this.gross = gross;}

    /**
     * 投资亮点
     */
    private String investSpot;
    @Basic
    public String getInvestSpot(){return investSpot;}
    public void setInvestSpot(String investSpot){this.investSpot = investSpot;}

    /**
    * 核心题材
    */
    private String coreTheme;
    @Basic
    public String getCoreTheme(){return coreTheme;}
    public void setCoreTheme(String coreTheme){this.coreTheme = coreTheme;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyInfoEntity that = (CompanyInfoEntity) o;

        if (businessScope != null ? !businessScope.equals(that.businessScope) : that.businessScope != null)
            return false;
        if (companyEnName != null ? !companyEnName.equals(that.companyEnName) : that.companyEnName != null)
            return false;
        if (companyName != null ? !companyName.equals(that.companyName) : that.companyName != null) return false;
        if (companyProfile != null ? !companyProfile.equals(that.companyProfile) : that.companyProfile != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (exchangeCenter != null ? !exchangeCenter.equals(that.exchangeCenter) : that.exchangeCenter != null)
            return false;
        if (fax != null ? !fax.equals(that.fax) : that.fax != null) return false;
        if (insititutionType != null ? !insititutionType.equals(that.insititutionType) : that.insititutionType != null)
            return false;
        if (issurePrice != null ? !issurePrice.equals(that.issurePrice) : that.issurePrice != null) return false;
        if (leadUnderWriter != null ? !leadUnderWriter.equals(that.leadUnderWriter) : that.leadUnderWriter != null)
            return false;
        if (listingDate != null ? !listingDate.equals(that.listingDate) : that.listingDate != null) return false;
        if (officeAddress != null ? !officeAddress.equals(that.officeAddress) : that.officeAddress != null)
            return false;
        if (organizationalForm != null ? !organizationalForm.equals(that.organizationalForm) : that.organizationalForm != null)
            return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        if (registeredAddress != null ? !registeredAddress.equals(that.registeredAddress) : that.registeredAddress != null)
            return false;
        if (registeredCapital != null ? !registeredCapital.equals(that.registeredCapital) : that.registeredCapital != null)
            return false;
        if (registrationDate != null ? !registrationDate.equals(that.registrationDate) : that.registrationDate != null)
            return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (website != null ? !website.equals(that.website) : that.website != null) return false;
        if (zipcode != null ? !zipcode.equals(that.zipcode) : that.zipcode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (stockCode != null ? stockCode.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (companyEnName != null ? companyEnName.hashCode() : 0);
        result = 31 * result + (exchangeCenter != null ? exchangeCenter.hashCode() : 0);
        result = 31 * result + (listingDate != null ? listingDate.hashCode() : 0);
        result = 31 * result + (issurePrice != null ? issurePrice.hashCode() : 0);
        result = 31 * result + (leadUnderWriter != null ? leadUnderWriter.hashCode() : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (registeredCapital != null ? registeredCapital.hashCode() : 0);
        result = 31 * result + (insititutionType != null ? insititutionType.hashCode() : 0);
        result = 31 * result + (organizationalForm != null ? organizationalForm.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (fax != null ? fax.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (zipcode != null ? zipcode.hashCode() : 0);
        result = 31 * result + (registeredAddress != null ? registeredAddress.hashCode() : 0);
        result = 31 * result + (officeAddress != null ? officeAddress.hashCode() : 0);
        result = 31 * result + (companyProfile != null ? companyProfile.hashCode() : 0);
        result = 31 * result + (businessScope != null ? businessScope.hashCode() : 0);
        return result;
    }
}
