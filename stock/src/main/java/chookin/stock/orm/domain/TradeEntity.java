package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by zhuyin on 5/23/15.
 */
@Entity
@Table(name = "trade", schema = "", catalog = "stock")
@IdClass(TradeEntityPK.class)
public class TradeEntity {
    private long id;
    private String stockCode;
    private Timestamp time;
    private Double price;
    private Double priceChange;
    private Double changeRatio;
    private Long tradeHand;
    private Long tradeValue;
    private Byte sell;

    @Basic
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Id
    @Column(name = "stockCode")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Id
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "priceChange")
    public Double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(Double priceChange) {
        this.priceChange = priceChange;
    }

    @Basic
    @Column(name = "changeRatio")
    public Double getChangeRatio() {
        return changeRatio;
    }

    public void setChangeRatio(Double changeRatio) {
        this.changeRatio = changeRatio;
    }

    @Basic
    @Column(name = "tradeHand")
    public Long getTradeHand() {
        return tradeHand;
    }

    public void setTradeHand(Long tradeHand) {
        this.tradeHand = tradeHand;
    }

    @Basic
    @Column(name = "tradeValue")
    public Long getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(Long tradeValue) {
        this.tradeValue = tradeValue;
    }

    @Basic
    @Column(name = "sell")
    public Byte getSell() {
        return sell;
    }

    public void setSell(Byte sell) {
        this.sell = sell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TradeEntity that = (TradeEntity) o;

        if (id != that.id) return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (priceChange != null ? !priceChange.equals(that.priceChange) : that.priceChange != null) return false;
        if (changeRatio != null ? !changeRatio.equals(that.changeRatio) : that.changeRatio != null) return false;
        if (tradeHand != null ? !tradeHand.equals(that.tradeHand) : that.tradeHand != null) return false;
        if (tradeValue != null ? !tradeValue.equals(that.tradeValue) : that.tradeValue != null) return false;
        if (sell != null ? !sell.equals(that.sell) : that.sell != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (stockCode != null ? stockCode.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (priceChange != null ? priceChange.hashCode() : 0);
        result = 31 * result + (changeRatio != null ? changeRatio.hashCode() : 0);
        result = 31 * result + (tradeHand != null ? tradeHand.hashCode() : 0);
        result = 31 * result + (tradeValue != null ? tradeValue.hashCode() : 0);
        result = 31 * result + (sell != null ? sell.hashCode() : 0);
        return result;
    }
}
