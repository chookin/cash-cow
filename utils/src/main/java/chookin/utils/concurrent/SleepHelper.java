package chookin.utils.concurrent;

import org.apache.log4j.Logger;

/**
 * Created by zhuyin on 1/6/15.
 */
public class SleepHelper {
    private final static Logger LOG = Logger.getLogger(SleepHelper.class);
    private static boolean doStop = false;
    public static void stop(){doStop = true;}
    public static void sleep(int millSeconds){
        long end = System.currentTimeMillis() + millSeconds;
        while (System.currentTimeMillis() < end && !doStop){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.warn(null, e);
            }
        }
    }
}
