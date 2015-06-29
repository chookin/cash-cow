package chookin.stock.orm.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by zhuyin on 6/29/15.
 */
public class IndexEntity implements Serializable {
    private String code;
    private String name;
    private Timestamp time;
    private Double point;
    private Double change;
    private Double changeRatio;
    /**成交量（手）*/
    private Long tradeHand;
    /**成交额(万元)*/
    private Long tradeValue;

    public String getCode() {
        return code;
    }

    public IndexEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public IndexEntity setName(String name) {
        this.name = name;
        return this;
    }

    public Timestamp getTime() {
        return time;
    }

    public IndexEntity setTime(Timestamp time) {
        this.time = time;
        return this;
    }

    public Double getPoint() {
        return point;
    }

    public IndexEntity setPoint(Double point) {
        this.point = point;
        return this;
    }
    public Double getChange() {
        return change;
    }

    public IndexEntity setChange(Double change) {
        this.change = change;
        return this;
    }

    public Double getChangeRatio() {
        return changeRatio;
    }

    public IndexEntity setChangeRatio(Double changeRatio) {
        this.changeRatio = changeRatio;
        return this;
    }

    public Long getTradeHand() {
        return tradeHand;
    }

    public IndexEntity setTradeHand(Long tradeHand) {
        this.tradeHand = tradeHand;
        return this;
    }

    public Long getTradeValue() {
        return tradeValue;
    }

    public IndexEntity setTradeValue(Long tradeValue) {
        this.tradeValue = tradeValue;
        return this;
    }

    @Override
    public String toString() {
        return "Index{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", point=" + point +
                ", change=" + change +
                ", changeRatio=" + changeRatio +
                ", tradeHand=" + tradeHand +
                ", tradeValue=" + tradeValue +
                '}';
    }
}
