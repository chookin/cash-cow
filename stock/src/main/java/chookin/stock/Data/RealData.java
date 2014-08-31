package chookin.stock.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chookin on 7/5/14.
 * http://walsece.iteye.com/blog/169514
 * String url = "http://hq.sinajs.cn/list=sh600151,sz000830,s_sh000001,s_sz399001,s_sz399106";
 */
public class RealData {
    public RealData(String id){
        this.id = id;
        this.open = -999999.0;
        this.yClose = -999999.0;

        this.change = -999999.0;
        this.changeRatio = -999999.0;
        this.curPrice = -999999.0;
        this.highPrice = -999999.0;
        this.lowPrice = -999999.0;
        this.marketValue = -999999.0;
        this.totalValue = -999999.0;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getyClose() {
        return yClose;
    }

    public void setyClose(double yClose) {
        this.yClose = yClose;
    }

    public double getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(double curPrice) {
        this.curPrice = curPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }
    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    private Date time;
    private String id;
    private double open;
    private double yClose;
    private double curPrice;
    private double highPrice;
    private double lowPrice;

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangeRatio() {
        return changeRatio;
    }

    public void setChangeRatio(double changeRatio) {
        this.changeRatio = changeRatio;
    }

    /**
     * price change
     */
    private double change;
    /**
     * price change ratio, in %
     */
    private double changeRatio;
    private double marketValue;

    public double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(double marketValue) {
        this.marketValue = marketValue;
    }

    private double totalValue;

    @Override
    public String toString() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate=sdf.format(this.getTime());
        return String.format("RealData{id: %s, time: %s, yClose: %.2f, open: %.2f, curPrice: %.2f, highPrice: %.2f, lowPrice: %.2f, change: %.2f, changeRatio: %.2f, marketValue: %.2f, totalValue: %.2f}", this.getId(), strDate, this.getyClose(), this.getOpen(), this.getCurPrice(), this.getHighPrice(), this.getLowPrice(), this.getChange(), this.getChangeRatio(), this.getMarketValue(), this.getTotalValue());
    }
}
