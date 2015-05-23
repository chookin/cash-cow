package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "history_data", schema = "", catalog = "stock")
@IdClass(HistoryDataEntityPK.class)
public class HistoryDataEntity{
    private long id;
    private String stockCode;
    private Date day;
    private Double openPrice;
    private Double closePrice;
    private Double highPrice;
    private Double lowPrice;
    private Long tradeHand;
    private Long tradeValue;
    @Basic
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockId) {
        this.stockCode = stockId;
    }

    @Id
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }
    public void setTime(java.util.Date day) {
        this.day = new Date(day.getTime());
    }
    @Basic
    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    @Basic
    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    @Basic
    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    @Basic
    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    @Basic
    public Long getTradeHand() {
        return tradeHand;
    }

    public void setTradeHand(Long tradeHand) {
        this.tradeHand = tradeHand;
    }

    @Basic
    public Long getTradeValue() {
        return tradeValue;
    }

    public void setTradeValue(Long tradeValue) {
        this.tradeValue = tradeValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryDataEntity that = (HistoryDataEntity) o;

        if (id != that.id) return false;
        if (stockCode != that.stockCode) return false;
        if (closePrice != null ? !closePrice.equals(that.closePrice) : that.closePrice != null) return false;
        if (highPrice != null ? !highPrice.equals(that.highPrice) : that.highPrice != null) return false;
        if (lowPrice != null ? !lowPrice.equals(that.lowPrice) : that.lowPrice != null) return false;
        if (openPrice != null ? !openPrice.equals(that.openPrice) : that.openPrice != null) return false;
        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        if (tradeHand != null ? !tradeHand.equals(that.tradeHand) : that.tradeHand != null) return false;
        if (tradeValue != null ? !tradeValue.equals(that.tradeValue) : that.tradeValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode.hashCode();
        result = 31 * result + (day != null ? day.hashCode() : 0);
        result = 31 * result + (openPrice != null ? openPrice.hashCode() : 0);
        result = 31 * result + (closePrice != null ? closePrice.hashCode() : 0);
        result = 31 * result + (highPrice != null ? highPrice.hashCode() : 0);
        result = 31 * result + (lowPrice != null ? lowPrice.hashCode() : 0);
        result = 31 * result + (tradeHand != null ? tradeHand.hashCode() : 0);
        result = 31 * result + (tradeValue != null ? tradeValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("HistoryData{code: %s, day: %s, openPrice: %.2f, closePrice: %.2f, highPrice: %.2f, lowPrice: %.2f, tradeHand: %d, tradeValue: %d}", this.getStockCode(), this.getDay(), this.getOpenPrice(), this.getClosePrice(), this.getHighPrice(), this.getLowPrice(), this.getTradeHand(), this.getTradeValue());
    }
}
