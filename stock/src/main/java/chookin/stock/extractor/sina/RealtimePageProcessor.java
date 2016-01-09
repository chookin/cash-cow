package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.RealtimeEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.TimeHelper;

/**
 * Created by zhuyin on 6/29/15.
 */
public class RealtimePageProcessor implements PageProcessor {
    static RealtimePageProcessor instance = new RealtimePageProcessor();
    public static Request getRequest(StockEntity stock){
        return new Request(String.format("http://hq.sinajs.cn/list=%s%s", stock.getExchange(), stock.getCode()), instance)
                .putExtra("stock", stock)
                .setTarget(Request.TargetResource.Json)
                .ignoreCache(true);
    }

    @Override
    public void process(ResultItems page) {
        StockEntity stock = page.getRequest().getExtra("stock", StockEntity.class);
        page.setField("realData", parse((String) page.getResource(), stock));
    }

    /**
     以大秦铁路（股票代码：601006）为例，如果要获取它的最新行情，只需访问新浪的股票数据
     接口：http://hq.sinajs.cn/list=sh601006这个url会返回一串文本，例如：
     var hq_str_sh601006="大秦铁路,27.55,27.25,26.91,27.55,26.20,26.91,26.92,22114263,589824680,4695,26.91,57590,26.90,14700,26.89,14300,26.88,15100,26.87,3100,26.92,8900,26.93,14230,26.94,25150,26.95,15220,26.96,2008-01-11,15:05:32";
     这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
     0：”大秦铁路”，股票名字；
     1：”27.55″，今日开盘价；
     2：”27.25″，昨日收盘价；
     3：”26.91″，当前价格；
     4：”27.55″，今日最高价；
     5：”26.20″，今日最低价；
     6：”26.91″，竞买价，即“买一”报价；
     7：”26.92″，竞卖价，即“卖一”报价；
     8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
     9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
     10：”4695″，“买一”申请4695股，即47手；
     11：”26.91″，“买一”报价；
     12：”57590″，“买二”
     13：”26.90″，“买二”
     14：”14700″，“买三”
     15：”26.89″，“买三”
     16：”14300″，“买四”
     17：”26.88″，“买四”
     18：”15100″，“买五”
     19：”26.87″，“买五”
     20：”3100″，“卖一”申报3100股，即31手；
     21：”26.92″，“卖一”报价
     (22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况”
     30：”2008-01-11″，日期；
     31：”15:05:32″，时间；
     * @param str page content
     * @return parsed result
     */
    private RealtimeEntity parse(String str, StockEntity stock){
        String text = str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\""));
        String[] arr = text.split(",");
        if(arr.length < 32){
            return null;
        }
        RealtimeEntity entity = new RealtimeEntity().setStockCode(stock.getCode());
        entity.setTime(TimeHelper.parseDate(arr[30] + arr[31], "yyyy-MM-ddHH:mm:ss").getTime());
        entity.setOpen(Double.valueOf(arr[1]));
        entity.setYclose(Double.valueOf(arr[2]));
        entity.setCurPrice(Double.valueOf(arr[3]));
        entity.setHighPrice(Double.valueOf(arr[4]));
        entity.setLowPrice(Double.valueOf(arr[5]));
        for(int i = 0; i<5; ++i){
            entity.addBuy(i, arr[10 + i * 2], arr[11 + i * 2]);
            entity.addSell(i, arr[20 + i * 2], arr[21 + i * 2]);
        }
        getLogger().trace(entity);
        return entity;
    }
}