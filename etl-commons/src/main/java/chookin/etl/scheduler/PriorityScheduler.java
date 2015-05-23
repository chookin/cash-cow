package chookin.etl.scheduler;

import chookin.etl.common.Request;

import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 3/12/15.
 *
 * Priority scheduler. Request with higher priority will be polled earlier.
 */
public class PriorityScheduler implements Scheduler {
    // to iterate sorted map in reverse order.
    private SortedMap<Integer, Queue<Request>> queueMap = new TreeMap<>(java.util.Collections.reverseOrder());
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    @Override
    public Scheduler push(Request request) {
        int priority = request.getPriority();
        lock.writeLock().lock();
        try{
            Queue queue = queueMap.get(priority);
            if(queue == null){
                queue = new LinkedBlockingDeque();
                queueMap.put(priority, queue);
            }
            queue.add(request);
        }finally {
            lock.writeLock().unlock();
        }
        return this;
    }

    @Override
    public Request poll() {
        lock.writeLock().lock();
        try{
            for(Map.Entry<Integer, Queue<Request>> pair : queueMap.entrySet()){
                if(pair.getValue().isEmpty()){
                    continue;
                }
                return pair.getValue().poll();
            }
        }finally {
            lock.writeLock().unlock();
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try{
            for(Map.Entry<Integer, Queue<Request>> pair : queueMap.entrySet()){
                if(pair.getValue().isEmpty()){
                    continue;
                }
                return false;
            }
        }finally {
            lock.readLock().unlock();
        }
        return true;
    }
}
