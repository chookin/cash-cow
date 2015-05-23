package chookin.stock.orm.domain;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by chookin on 7/28/14.
 * 每一笔交易,包括价格，手数，买或卖，交易时间
 */
public class HistoryDayDetailEntity {
    private String stockCode;
    private Date date;
    private SortedMap<Date, Exchange> exchanges = new TreeMap<>();
    public static class Exchange{
        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public long getTradeHand() {
            return tradeHand;
        }

        public void setTradeHand(long tradeHand) {
            this.tradeHand = tradeHand;
        }

        public long getTradeValue() {
            return tradeValue;
        }

        public void setTradeValue(long tradeValue) {
            this.tradeValue = tradeValue;
        }

        public boolean isSell() {
            return sell;
        }

        public void setSell(boolean sell) {
            this.sell = sell;
        }

        private Date time;
        private double price;
        private long tradeHand;
        private long tradeValue;
        private boolean sell;
    }
}
