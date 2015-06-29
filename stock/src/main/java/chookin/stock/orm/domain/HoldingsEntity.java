package chookin.stock.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zhuyin on 6/15/15.
 */
@Entity
@Table(name = "holdings", schema = "", catalog = "stock")
public class HoldingsEntity implements Serializable {
    private long id;
    private String stockCode;
    private Timestamp time;
    private Double price;
    private Long hand;
    private Double hial;
    private Double loal;
    private Boolean valid;

    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "stockCode")
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
    @Column(name = "price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "hand")
    public Long getHand() {
        return hand;
    }

    public void setHand(Long hand) {
        this.hand = hand;
    }

    @Basic
    @Column(name = "hial")
    public Double getHial() {
        return hial;
    }

    public void setHial(Double hial) {
        this.hial = hial;
    }

    @Basic
    @Column(name = "loal")
    public Double getLoal() {
        return loal;
    }

    public void setLoal(Double loal) {
        this.loal = loal;
    }

    @Basic
    @Column(name = "valid")
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HoldingsEntity that = (HoldingsEntity) o;

        if (id != that.id) return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (hand != null ? !hand.equals(that.hand) : that.hand != null) return false;
        if (hial != null ? !hial.equals(that.hial) : that.hial != null) return false;
        if (loal != null ? !loal.equals(that.loal) : that.loal != null) return false;
        if (valid != null ? !valid.equals(that.valid) : that.valid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (stockCode != null ? stockCode.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (hand != null ? hand.hashCode() : 0);
        result = 31 * result + (hial != null ? hial.hashCode() : 0);
        result = 31 * result + (loal != null ? loal.hashCode() : 0);
        result = 31 * result + (valid != null ? valid.hashCode() : 0);
        return result;
    }
}
