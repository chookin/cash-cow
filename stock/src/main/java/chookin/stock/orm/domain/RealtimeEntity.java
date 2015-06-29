package chookin.stock.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by chookin on 7/28/14.
 */
@Entity
@Table(name = "realtime", schema = "", catalog = "stock")
public class RealtimeEntity implements Serializable {
    private String stockCode;
    private Timestamp time;
    private Double open;
    private Double yclose;
    private Double curPrice;
    private Double highPrice;
    private Double lowPrice;

    @Id
    public String getStockCode() {
        return stockCode;
    }

    public RealtimeEntity setStockCode(String stockCode) {
        this.stockCode = stockCode;
        return this;
    }

    @Basic
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setTime(long time) {
        this.time = new Timestamp(time);
    }

    @Basic
    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    @Basic
    public Double getYclose() {
        return yclose;
    }

    public void setYclose(Double yclose) {
        this.yclose = yclose;
    }

    @Basic
    public Double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(Double curPrice) {
        this.curPrice = curPrice;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealtimeEntity that = (RealtimeEntity) o;

        if (curPrice != null ? !curPrice.equals(that.curPrice) : that.curPrice != null) return false;
        if (highPrice != null ? !highPrice.equals(that.highPrice) : that.highPrice != null) return false;
        if (lowPrice != null ? !lowPrice.equals(that.lowPrice) : that.lowPrice != null) return false;
        if (open != null ? !open.equals(that.open) : that.open != null) return false;
        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (yclose != null ? !yclose.equals(that.yclose) : that.yclose != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode != null ? stockCode.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (open != null ? open.hashCode() : 0);
        result = 31 * result + (yclose != null ? yclose.hashCode() : 0);
        result = 31 * result + (curPrice != null ? curPrice.hashCode() : 0);
        result = 31 * result + (highPrice != null ? highPrice.hashCode() : 0);
        result = 31 * result + (lowPrice != null ? lowPrice.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate=sdf.format(this.getTime());
        return String.format("RealData{code: %s, time: %s, yClose: %.2f, open: %.2f, curPrice: %.2f, highPrice: %.2f, lowPrice: %.2f}", this.getStockCode(), strDate, this.getYclose(), this.getOpen(), this.getCurPrice(), this.getHighPrice(), this.getLowPrice());
    }
}
