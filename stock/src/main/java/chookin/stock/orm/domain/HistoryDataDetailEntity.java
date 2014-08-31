package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "history_data_detail", schema = "", catalog = "stock")
public class HistoryDataDetailEntity {
    private long id;
    private int stockId;
    private Timestamp time;
    private Double price;
    private Double priceChange;
    private Double changeRatio;
    private Long tradeHand;
    private Long tradeValue;
    private Byte isSell;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "stock_id")
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
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
    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    @Basic
    @Column(name = "is_sell")
    public Byte getIsSell() {
        return isSell;
    }

    public void setIsSell(Byte isSell) {
        this.isSell = isSell;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryDataDetailEntity that = (HistoryDataDetailEntity) o;

        if (id != that.id) return false;
        if (stockId != that.stockId) return false;
        if (changeRatio != null ? !changeRatio.equals(that.changeRatio) : that.changeRatio != null) return false;
        if (isSell != null ? !isSell.equals(that.isSell) : that.isSell != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (priceChange != null ? !priceChange.equals(that.priceChange) : that.priceChange != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (tradeHand != null ? !tradeHand.equals(that.tradeHand) : that.tradeHand != null) return false;
        if (tradeValue != null ? !tradeValue.equals(that.tradeValue) : that.tradeValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockId;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (priceChange != null ? priceChange.hashCode() : 0);
        result = 31 * result + (changeRatio != null ? changeRatio.hashCode() : 0);
        result = 31 * result + (tradeHand != null ? tradeHand.hashCode() : 0);
        result = 31 * result + (tradeValue != null ? tradeValue.hashCode() : 0);
        result = 31 * result + (isSell != null ? isSell.hashCode() : 0);
        return result;
    }
}
