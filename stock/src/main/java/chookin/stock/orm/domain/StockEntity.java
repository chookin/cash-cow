package chookin.stock.orm.domain;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "stock", schema = "", catalog = "stock")
public class StockEntity {
    private int stockId;
    private String stockCode;
    private String stockName;
    private String exchange;
    private boolean discarded = false;
    private Timestamp updateTime= new Timestamp(System.currentTimeMillis());
    @Basic
    @Column(name = "stock_id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Id
    @Column(name = "stock_code")
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Basic
    @Column(name = "stock_name")
    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    @Basic
    @Column(name = "exchange")
    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Basic
    @Column
    public boolean getDiscarded(){ return discarded;}

    public void setDiscarded(boolean discarded){this.discarded = discarded;}

    @Basic
    @Column(name = "update_time")
    public Timestamp getUpdateTime(){return updateTime;}

    public void setUpdateTime(Timestamp updateTime){this.updateTime = updateTime; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockEntity that = (StockEntity) o;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (stockCode != null ? stockCode.hashCode() : 0);
    }

    @Override
    public String toString() {
        return String.format("Stock{code: %s, name: %s, exchange: %s}",this.stockCode, this.stockName, this.exchange);
    }
}
