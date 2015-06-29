package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.IndexEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;

import java.sql.Timestamp;

/**
 * Created by zhuyin on 6/29/15.
 */
public class IndexPageProcessor implements PageProcessor {
    static IndexPageProcessor instance = new IndexPageProcessor();
    public static Request getRequest(){
        return new Request("http://hq.sinajs.cn/list=s_sh000001", instance)
                .setTarget(Request.TargetResource.Json)
                .ignoreCache(true);
    }

    @Override
    public void process(ResultItems page) {
        page.setField("index", parse((String) page.getResource()));
    }

    /**
     * 如果你要查询大盘指数，情况会有不同，比如查询上证综合指数（000001），使用如下URL：
     http://hq.sinajs.cn/list=s_sh000001 服务器返回的数据为：
     var hq_str_s_sh000001="上证指数,4053.030,-139.843,-3.34,6737863,90427139";
     数据含义分别为：
     0: 指数名称;
     1: 当前点数;
     2: 涨跌额;
     3: 涨跌率;
     4: 成交量（手）;
     5: 成交额（万元）；
     */
    private IndexEntity parse(String str){
        String text = str.substring(str.indexOf("\"")+1, str.lastIndexOf("\""));
        String[] arr = text.split(",");
        if(arr.length < 5){
            return null;
        }
        return new IndexEntity().setCode("000001")
                .setName(arr[0])
                .setPoint(Double.valueOf(arr[1]))
                .setChange(Double.valueOf(arr[2]))
                .setChangeRatio(Double.valueOf(arr[3]))
                .setTradeHand(Long.valueOf(arr[4]))
                .setTradeValue(Long.valueOf(arr[5]))
                .setTime(new Timestamp(System.currentTimeMillis()))
                ;
    }
}
