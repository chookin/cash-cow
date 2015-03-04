package chookin.etl.scheduler;

import chookin.etl.common.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by zhuyin on 3/2/15.
 */
public class QueueScheduler {
    private BlockingQueue<Request> queue = new LinkedBlockingDeque<>();
    public QueueScheduler push(Request request){
        queue.add(request);
        return this;
    }
    public Request poll(){
        return queue.poll();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
