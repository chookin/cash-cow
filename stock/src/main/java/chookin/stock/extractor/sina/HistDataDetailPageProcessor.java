package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.StockEntity;
import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.utils.web.UrlHelper;
import cmri.etl.processor.PageProcessor;
import cmri.utils.lang.TimeHelper;

import java.util.Date;

/**
 * Created by zhuyin on 5/20/15.
 */
public class HistDataDetailPageProcessor implements PageProcessor{
    static final HistDataDetailPageProcessor PROCESSOR = new HistDataDetailPageProcessor();

    public static Request getRequest(StockEntity stock, Date date){
        // url such as http://market.finance.sina.com.cn/downxls.php?date=2015-01-27&symbol=sz000001
        String url = String.format("http://market.finance.sina.com.cn/downxls.php?date=%s&symbol=%s%s", TimeHelper.toString(date, "yyyy-MM-dd"), stock.getExchange(),stock.getCode());
        return new Request(url, PROCESSOR)
                .putExtra("stock", stock)
                .putExtra("date", date)
                .setTarget(Request.TargetResource.File)
                .setFilePath(String.format("%s/%s/%s.dat", UrlHelper.getLocalPath(), stock, TimeHelper.toString(date, "yyyy-MM-dd")))
                ;
    }
    @Override
    public void process(ResultItems page) {

    }
}
