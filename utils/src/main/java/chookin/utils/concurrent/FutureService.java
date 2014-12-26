package chookin.utils.concurrent;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zhuyin on 12/26/14.
 */
public class FutureService<T> {
    private static Logger LOG = Logger.getLogger(FutureService.class);
    private ExecutorService executorService;
    private int poolSize;
    private Collection<T> items;
    private CallableCreator<T> creator;
    public FutureService(Collection<T> items, CallableCreator<T> creator, int poolSize){
        this.items = items;
        this.creator = creator;
        this.poolSize = poolSize;
    }
    ArrayList<T>[] shuffle(Collection<T> categoryEntities){
        ArrayList<T>[] batchEntity = new ArrayList[this.poolSize];
        for(int i = 0; i< batchEntity.length;++i){
            batchEntity[i] = new ArrayList<>();
        }
        int index =0;
        for(T entity: categoryEntities){
            batchEntity[index % poolSize].add(entity);
            ++index;
        }
        return batchEntity;
    }
    public static interface CallableCreator<T>{
        Callable<Integer> create(Collection<T> items);
    }
    List<Future<Integer>> createFutures(Collection<T> items, CallableCreator<T> creator){
        ArrayList<T>[] batchEntity = shuffle(items);
        executorService = Executors.newFixedThreadPool(poolSize);
        List<Future<Integer>> futures = new ArrayList<>();
        for(Collection<T> batch : batchEntity){
            futures.add(executorService
                    .submit(creator.create(batch)));
        }
        return futures;
    }

    int getFutureResult(Collection<Future<Integer>> futures){
        int count = 0;
        while (true){
            boolean allDone = true;
            for(Future<Integer> future: futures){
                try {
                    if (future.isDone() && !future.isCancelled()) {
                        count += future.get();
                    }else {
                        allDone = false;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error(future, e);
                    future.cancel(true);
                }
            }
            if(allDone){
                break;
            }else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOG.warn(null, e);
                }
            }
        }
        this.executorService.shutdown();
        return count;
    }
    public int action(){
        List<Future<Integer>> futures = createFutures(items, creator);
        return getFutureResult(futures);
    }
}
