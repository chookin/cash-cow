package chookin.etl.scheduler;

import chookin.etl.common.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by zhuyin on 3/2/15.
 */
public class QueueScheduler implements Scheduler{
    private BlockingQueue<Request> queue = new LinkedBlockingDeque<>();

    @Override
    public QueueScheduler push(Request request){
        queue.add(request);
        return this;
    }

    @Override
    public Request poll(){
        return queue.poll();
    }

    @Override
    public boolean isEmpty(){
        return queue.isEmpty();
    }
}
