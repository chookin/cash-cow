package chookin.stock.Data;

import java.util.Date;

/**
 * Created by chookin on 7/5/14.
 * http://vip.stock.finance.sina.com.cn/quotes_service/view/vMS_tradehistory.php?symbol=sz000028&date=2014-07-03
 */
public class DealDetail {
    private Date date;
    private double price;
    private double priceChangeRatio;
    private int turnover;
    private double amount;
    private boolean isSell;
}
