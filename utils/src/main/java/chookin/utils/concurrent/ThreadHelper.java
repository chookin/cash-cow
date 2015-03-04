package chookin.utils.concurrent;

import org.apache.log4j.Logger;

/**
 * Created by zhuyin on 1/6/15.
 */
public class ThreadHelper {
    private final static Logger LOG = Logger.getLogger(ThreadHelper.class);
    public static void sleep(int millSeconds){
        long end = System.currentTimeMillis() + millSeconds;
        while (System.currentTimeMillis() < end){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.warn(null, e);
            }
        }
    }
}
