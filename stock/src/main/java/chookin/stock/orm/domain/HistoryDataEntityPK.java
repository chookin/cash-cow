package chookin.stock.orm.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by chookin on 7/28/14.
 */
public class HistoryDataEntityPK implements Serializable {
    private int stockId;
    private Date time;

    @Column(name = "stock_id")
    @Id
    public int getStockCode() {
        return stockId;
    }

    public void setStockCode(int stockId) {
        this.stockId = stockId;
    }

    @Column(name = "time")
    @Id
    public Date getDay() {
        return time;
    }

    public void setDay(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryDataEntityPK that = (HistoryDataEntityPK) o;

        if (stockId != that.stockId) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockId;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
