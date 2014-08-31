package chookin.stock.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "history_data", schema = "", catalog = "stock")
@IdClass(HistoryDataEntityPK.class)
public class HistoryDataEntity{
    private long id;
    private int stockId;
    private Date time;
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
    @Column(name = "stock_id")
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Id
    @Column(name = "time")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Basic
    @Column(name = "open_price")
    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    @Basic
    @Column(name = "close_price")
    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
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
    @Column(name = "trade_hand")
    public Long getTradeHand() {
        return tradeHand;
    }

    public void setTradeHand(Long tradeHand) {
        this.tradeHand = tradeHand;
    }

    @Basic
    @Column(name = "trade_value")
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
        if (stockId != that.stockId) return false;
        if (closePrice != null ? !closePrice.equals(that.closePrice) : that.closePrice != null) return false;
        if (highPrice != null ? !highPrice.equals(that.highPrice) : that.highPrice != null) return false;
        if (lowPrice != null ? !lowPrice.equals(that.lowPrice) : that.lowPrice != null) return false;
        if (openPrice != null ? !openPrice.equals(that.openPrice) : that.openPrice != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (tradeHand != null ? !tradeHand.equals(that.tradeHand) : that.tradeHand != null) return false;
        if (tradeValue != null ? !tradeValue.equals(that.tradeValue) : that.tradeValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockId;
        result = 31 * result + (time != null ? time.hashCode() : 0);
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
        return String.format("HistoryData{id: %s, time: %s, openPrice: %.2f, closePrice: %.2f, highPrice: %.2f, lowPrice: %.2f, tradeHand: %d, tradeValue: %d}", this.getStockId(), this.getTime(), this.getOpenPrice(), this.getClosePrice(), this.getHighPrice(), this.getLowPrice(), this.getTradeHand(), this.getTradeValue());
    }
}
