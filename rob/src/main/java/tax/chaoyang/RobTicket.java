package tax.chaoyang;

import cmri.etl.spider.Spider;
import service.BaseOper;

/**
 * Created by zhuyin on 6/10/15.
 */
public class RobTicket extends BaseOper {
    @Override
    public boolean action() {
        long count = 0;
        while (true) {
            new Spider().addRequest(DatePageProcessor.getSeedRequests())
                    .run();
            ++count;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
