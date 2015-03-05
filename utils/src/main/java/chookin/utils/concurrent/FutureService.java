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

    public long action(){
        List<Future<Long>> futures = createFutures(items, creator);
        return getFutureResult(futures);
    }

    List<Future<Long>> createFutures(Collection<T> items, CallableCreator<T> creator){
        ArrayList<T>[] batchEntity = shuffle(items);
        executorService = Executors.newFixedThreadPool(poolSize);
        List<Future<Long>> futures = new ArrayList<>();
        for(Collection<T> batch : batchEntity){
            futures.add(executorService
                    .submit(creator.create(batch)));
        }
        return futures;
    }

    public static interface CallableCreator<T>{
        Callable<Long> create(Collection<T> items);
    }

    @SuppressWarnings("unchecked")
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

    long getFutureResult(Collection<Future<Long>> futures){
        int count = 0;
        while (true){
            boolean allDone = true;
            for(Future<Long> future: futures){
                try {
                    if (future.isDone() && !future.isCancelled()) {
                        count += future.get();
                    }else {
                        allDone = false;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOG.error(future, e);
                    // future.cancel(true);
                }
            }
            if(allDone){
                break;
            }else {
                ThreadHelper.sleep(1000);
            }
        }
        this.executorService.shutdown();
        return count;
    }
}
