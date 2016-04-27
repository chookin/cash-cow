package chookin.stock.orm.domain;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by chookin on 7/28/14.
 */
public class HistoryEntityPK implements Serializable {
    private String stockCode;
    private Date day;

    @Id
    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    @Id
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HistoryEntityPK that = (HistoryEntityPK) o;

        if (stockCode != null ? !stockCode.equals(that.stockCode) : that.stockCode != null) return false;
        if (day != null ? !day.equals(that.day) : that.day != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = stockCode.hashCode();
        result = 31 * result + (day != null ? day.hashCode() : 0);
        return result;
    }
}
