package chookin.stock.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "stock", schema = "", catalog = "stock")
public class StockEntity implements Serializable {
    private String code;
    private String name;
    private String exchange;
    private boolean discard = false;
    private Timestamp updateTime= new Timestamp(System.currentTimeMillis());

    @Id
    @Column
    public String getCode() {
        return code;
    }

    public void setCode(String stockId) {
        this.code = stockId;
    }

    @Basic
    @Column
    public String getName() {
        return name;
    }

    public void setName(String stockName) {
        this.name = stockName;
    }

    @Basic
    @Column
    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Basic
    @Column
    public boolean getDiscard(){ return discard;}

    public void setDiscard(boolean discarded){this.discard = discarded;}

    @Basic
    @Column
    public Timestamp getUpdateTime(){return updateTime;}

    public void setUpdateTime(Timestamp updateTime){this.updateTime = updateTime; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockEntity that = (StockEntity) o;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (code != null ? code.hashCode() : 0);
    }

    @Override
    public String toString() {
        return String.format("Stock{code: %s, name: %s, exchange: %s}",this.code, this.name, this.exchange);
    }
}
