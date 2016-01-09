package chookin.stock.extractor.sina;

import chookin.stock.orm.domain.HistoryDayDetailEntity;
import cmri.utils.lang.TimeHelper;
import cmri.utils.lang.StringHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuyin on 5/21/15.
 */
public class HistDataDetailResource {
    /**
     the source file format such as:
     成交时间	成交价	价格变动	成交量(手)	成交额(元)	性质
     15:00:20	10.61	--	5250	5570419	买盘
     14:57:02	10.61	--	10	10610	卖盘
     14:56:59	10.61	0.01	107	113569	买盘

     * @param fileName the name of history detail csv file. file name format:/home/chookin/stock/market.finance.sina.com.cn/2014-11-18/sz000001.dat
     * @return the retrieved entities.
     */
    public List<HistoryDayDetailEntity> extractFile(String fileName) throws IOException {
        List<HistoryDayDetailEntity> entities = new ArrayList<>();
        String absFileName = new File(fileName).getAbsolutePath();
        String stockCode = StringHelper.parseRegex(absFileName, "sz([\\d]+).dat", 1);
        String strdate = StringHelper.parseRegex(absFileName, "(\\d{4}-\\d{2}-\\d{2})", 1);
        Date date = TimeHelper.parseDate(strdate);

        List<String> lines = FileUtils.readLines(new File(fileName), "gbk");
        int count = -1;
        for(String line: lines){
            count += 1;
            if( count == 0 ) {
                continue; // ignore the head line
            }
            String[] items = line.split("\t");
            // TODO
        }
        return entities;
    }
}
