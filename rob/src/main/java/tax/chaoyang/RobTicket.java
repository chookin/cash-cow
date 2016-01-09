package tax.chaoyang;

import cmri.etl.spider.SpiderAdapter;
import cmri.utils.concurrent.ThreadHelper;

/**
 * Created by zhuyin on 6/10/15.
 */
public class RobTicket extends cmri.utils.lang.BaseOper {
    @Override
    public boolean action() {
        long count = 0;
        while (true) {
            new SpiderAdapter().addRequest(DatePageProcessor.getSeedRequests())
                    .run();
            ++count;
            ThreadHelper.sleep(5000);
            if(count > 100000000){
                break;
            }
        }
        return true;
    }


    public static void main(String[] args){
        new RobTicket().action();
    }
}
