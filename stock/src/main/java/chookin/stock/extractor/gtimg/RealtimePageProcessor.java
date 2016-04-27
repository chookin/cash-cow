package chookin.stock.extractor.gtimg;

import chookin.stock.orm.domain.RealtimeEntity;
import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.TimeHelper;

/**
 * Created by zhuyin on 3/21/15.
 */
public class RealtimePageProcessor implements PageProcessor {
    static RealtimePageProcessor processor = new RealtimePageProcessor();
    public static Request getRequest(StockEntity stock){
        // http://qt.gtimg.cn/q=sh600068
        return new Request(String.format("http://qt.gtimg.cn/q=%s%s", stock.getExchange(), stock.getCode()), processor)
                .putExtra("stock", stock)
                .setTarget(Request.TargetResource.Json)
                .addHeader("Pragma", "no-cache")
                .addHeader("Accept-Encoding", "gzip, deflate, sdch")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6,es;q=0.4")
                .addHeader("header", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("header", "Cache-Control: no-cache")
                .addHeader("header", "Connection: keep-alive")
                .setValidPeriod(0L);
    }

    @Override
    public void process(ResultItems page) {
        StockEntity stock = page.getRequest().getExtra("stock", StockEntity.class);
        page.setField("realData", parse((String) page.getResource(), stock));
    }

    /**
     以 ~ 分割字符串中内容，下标从0开始，依次为
     *  0: 未知
     *  1: 名字
     *  2: 代码
     *  3: 当前价格
     *  4: 昨收
     *  5: 今开
     *  6: 成交量（手）
     *  7: 外盘
     *  8: 内盘
     *  9: 买一
     * 10: 买一量（手）
     * 11-18: 买二 买五
     * 19: 卖一
     * 20: 卖一量
     * 21-28: 卖二 卖五
     * 29: 最近逐笔成交
     * 30: 时间
     * 31: 涨跌
     * 32: 涨跌%
     * 33: 最高
     * 34: 最低
     * 35: 价格/成交量（手）/成交额
     * 36: 成交量（手）
     * 37: 成交额（万）
     * 38: 换手率
     * 39: 市盈率
     * 40:
     * 41: 最高
     * 42: 最低
     * 43: 振幅
     * 44: 流通市值
     * 45: 总市值
     * 46: 市净率
     * 47: 涨停价
     * 48: 跌停价
     * @param str page content
     * @return parsed result
     */
    private RealtimeEntity parse(String str, StockEntity stock){
        String[] arr = str.split("~");
        if(arr.length < 49){
            return null;
        }
        RealtimeEntity entity = new RealtimeEntity().setStockCode(stock.getCode());
        entity.setTime(TimeHelper.parseDate(arr[30], "yyyyMMddHHmmss").getTime());// 20150626150255
        entity.setCurPrice(Double.valueOf(arr[3]));
        entity.setYclose(Double.valueOf(arr[4]));
        entity.setOpen(Double.valueOf(arr[5]));
        entity.setHighPrice(Double.valueOf(arr[33]));
        entity.setLowPrice(Double.valueOf(arr[34]));
        for(int i = 0; i<5; ++i){
            entity.addBuy(i, arr[10 + i * 2], arr[9 + i * 2]);
            entity.addSell(i, arr[20 + i * 2], arr[19 + i * 2]);
        }
        getLogger().trace(entity);
        return entity;
    }
}
