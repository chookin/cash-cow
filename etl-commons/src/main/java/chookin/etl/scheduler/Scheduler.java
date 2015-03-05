package chookin.etl.scheduler;

import chookin.etl.common.Request;

/**
 * Created by zhuyin on 3/5/15.
 */
public interface Scheduler {
    QueueScheduler push(Request request);
    Request poll();

    boolean isEmpty();
}
