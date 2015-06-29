package chookin.stock.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * Created by zhuyin on 1/18/15.
 */
@Entity
@Table(name = "holiday", schema = "", catalog = "stock")
public class HolidayEntity implements Serializable {
    private Date day;
    private String descr;

    @Id
    @Column(name = "day")
    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    @Basic
    @Column(name = "descr")
    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HolidayEntity that = (HolidayEntity) o;

        if (day != null ? !day.equals(that.day) : that.day != null) return false;
        if (descr != null ? !descr.equals(that.descr) : that.descr != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = day != null ? day.hashCode() : 0;
        result = 31 * result + (descr != null ? descr.hashCode() : 0);
        return result;
    }
}
