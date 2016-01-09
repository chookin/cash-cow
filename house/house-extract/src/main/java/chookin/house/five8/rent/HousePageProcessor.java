package chookin.house.five8.rent;

import cmri.etl.common.Request;
import cmri.etl.common.ResultItems;
import cmri.etl.processor.PageProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuyin on 4/1/15.
 */
public class HousePageProcessor implements PageProcessor {
    private static final Log LOG = LogFactory.getLog(HousePageProcessor.class);
    static PageProcessor processor = new HousePageProcessor();

    public static Set<Request> getSeedRequests(){
        Set<Request> requests = new HashSet<>();
        requests.add(new Request("http://bj.58.com/chuzu/")
                        .setPageProcessor(processor)
                        .putExtra("category", "rent")
        );
        return requests;
    }
    @Override
    public void process(ResultItems page) {

    }
}
