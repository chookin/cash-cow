package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "real_time", schema = "", catalog = "stock")
public class RealTimeEntity {
    private String stockCode;
    private Timestamp time;
    private Double open;
    private Double yclose;
    private Double priceChange;
    private Double changeRatio;
    private Double curPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double marketValue;
    private Double totalValue;

    @Id
    @Column(name = "stock_code")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Basic
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "open")
    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    @Basic
    @Column(name = "yclose")
    public Double getYclose() {
        return yclose;
    }

    public void setYclose(Double yclose) {
        this.yclose = yclose;
    }

    @Basic
    @Column(name = "price_change")
    public Double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(Double priceChange) {
        this.priceChange = priceChange;
    }

    @Basic
    @Column(name = "change_ratio")
    public Double getChangeRatio() {
        return changeRatio;
    }

    public void setChangeRatio(Double changeRatio) {
        this.changeRatio = changeRatio;
    }

    @Basic
    @Column(name = "cur_price")
    public Double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(Double curPrice) {
        this.curPrice = curPrice;
    }

    @Basic
    @Column(name = "high_price")
    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    @Basic
    @Column(name = "low_price")
    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    @Basic
    @Column(name = "market_value")
    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    @Basic
    @Column(name = "total_value")
    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealTimeEntity that = (RealTimeEntity) o;

        if (changeRatio != null ? !changeRatio.equals(that.changeRatio) : that.changeRatio != null) return false;
        if (curPrice != null ? !curPrice.equals(that.curPrice) : that.curPrice != null) return false;
        if (highPrice != null ? !highPrice.equals(that.highPrice) : that.highPrice != null) return false;
        if (lowPrice != null ? !lowPrice.equals(that.lowPrice) : that.lowPrice != null) return false;
        if (marketValue != null ? !marketValue.equals(that.marketValue) : that.marketValue != null) return false;
        if (open != null ? !open.equals(that.open) : that.open != null) return false;
        if (priceChange != null ? !priceChange.equals(that.priceChange) : that.priceChange != null) return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (totalValue != null ? !totalValue.equals(that.totalValue) : that.totalValue != null) return false;
        if (yclose != null ? !yclose.equals(that.yclose) : that.yclose != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (open != null ? open.hashCode() : 0);
        result = 31 * result + (yclose != null ? yclose.hashCode() : 0);
        result = 31 * result + (priceChange != null ? priceChange.hashCode() : 0);
        result = 31 * result + (changeRatio != null ? changeRatio.hashCode() : 0);
        result = 31 * result + (curPrice != null ? curPrice.hashCode() : 0);
        result = 31 * result + (highPrice != null ? highPrice.hashCode() : 0);
        result = 31 * result + (lowPrice != null ? lowPrice.hashCode() : 0);
        result = 31 * result + (marketValue != null ? marketValue.hashCode() : 0);
        result = 31 * result + (totalValue != null ? totalValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate=sdf.format(this.getTime());
        return String.format("RealData{id: %s, time: %s, yClose: %.2f, open: %.2f, curPrice: %.2f, highPrice: %.2f, lowPrice: %.2f, change: %.2f, changeRatio: %.2f, marketValue: %.2f, totalValue: %.2f}", this.getStockCode(), strDate, this.getYclose(), this.getOpen(), this.getCurPrice(), this.getHighPrice(), this.getLowPrice(), this.getPriceChange(), this.getChangeRatio(), this.getMarketValue(), this.getTotalValue());
    }
}
