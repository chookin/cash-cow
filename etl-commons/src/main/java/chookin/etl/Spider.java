package chookin.etl;

import chookin.etl.common.Request;
import chookin.etl.common.ResultItems;
import chookin.etl.downloader.Downloader;
import chookin.etl.downloader.JsoupDownloader;
import chookin.etl.pipeline.ConsolePipeline;
import chookin.etl.pipeline.Pipeline;
import chookin.etl.processor.PageProcessor;
import chookin.etl.scheduler.QueueScheduler;
import chookin.utils.concurrent.CountableThreadPool;
import chookin.utils.concurrent.ThreadHelper;
import chookin.utils.web.NetworkHelper;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhuyin on 3/2/15.
 */
public class Spider implements Runnable {
    private static final Logger LOG = Logger.getLogger(Spider.class);
    private QueueScheduler scheduler = new QueueScheduler();
    private Set<Pipeline> pipelines = new HashSet<>();
    private String userAgent;
    private long validateMilliSeconds;
    private int timeOut = 60000;
    private int sleepMillisecond = 100000;
    private Downloader downloader;
    private PageProcessor pageProcessor;
    private String uuid;
    private Date startTime;
    private final AtomicLong pageCount = new AtomicLong(0);
    private CountableThreadPool threadPool;
    private ExecutorService executorService;
    private int threadNum = 1;
    private Status stat = Status.Init;
    public Spider setUserAgent(String userAgent){
        this.userAgent = userAgent;
        return this;
    }
    public String getUserAgent(){
        return this.userAgent;
    }

    /**
     * If validateMiliSeconds > 0, then get document from internet or from local disk.
     * @param validateSeconds The valid period of the downloaded file for this url. If local file expired, will download again. Unit is second.
     * @return
     */
    public Spider setValidateSeconds(long validateSeconds){
        if(validateSeconds < 0 || validateSeconds == Long.MAX_VALUE){
            this.validateMilliSeconds = Long.MAX_VALUE;
        }else{
            this.validateMilliSeconds = validateSeconds * 1000L;
        }
        return this;
    }

    public long getValidateMilliSeconds(){
        return this.validateMilliSeconds;
    }
    public String getUUID() {
        if (uuid != null) {
            return uuid;
        }
        uuid = UUID.randomUUID().toString();
        return uuid;
    }
    /**
     *
     * @param timeOut Unit is seconds.
     * @return
     */
    public Spider setTimeOut(int timeOut){
        this.timeOut = timeOut;
        return this;
    }

    public Spider setSleepMillisecond(int sleepMillisecond){
        this.sleepMillisecond = sleepMillisecond;
        return this;
    }

    public Spider setPageProcessor(PageProcessor pageProcessor){
        checkIfRunning();
        this.pageProcessor = pageProcessor;
        return this;
    }
    /**
     * Set the downloader of spider
     *
     * @param downloader
     * @return this
     * @see Downloader
     */
    public Spider setDownloader(Downloader downloader) {
        checkIfRunning();
        this.downloader = downloader;
        return this;
    }
    private void checkRunningStat() {
        if (stat == Status.Running) {
            throw new IllegalStateException("Spider is already running!");
        }
        stat = Status.Running;
    }
    private void checkIfRunning() {
        if (stat == Status.Running) {
            throw new IllegalStateException("Spider is already running!");
        }
    }
    /**
     * start with more than one threads
     *
     * @param threadNum the new thread num.
     * @return this
     */
    public Spider thread(int threadNum) {
        checkIfRunning();
        this.threadNum = threadNum;
        if (threadNum <= 0) {
            throw new IllegalArgumentException("threadNum should be more than zero!");
        }
        return this;
    }
    /**
     *
     * @return Unit is seconds.
     */
    public int getTimeOut(){
        return this.timeOut;
    }
    /**
     * Get thread count which is running
     *
     * @return thread count which is running
     */
    public int getThreadAlive() {
        if (threadPool == null) {
            return 0;
        }
        return threadPool.getThreadAlive();
    }
    public Date getStartTime() {
        return startTime;
    }
    /**
     * Get running status by spider.
     *
     * @return running status
     * @see Status
     */
    public Status getStatus() {
        return stat;
    }
    public enum Status {
        Init(0),
        Running(1),
        Stopped(2);
        private Status(int value) {
            this.value = value;
        }
        private int value;
        public int getValue() {
            return value;
        }
        public static Status fromValue(int value) {
            for (Status status : Status.values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            //default value
            return Init;
        }
    }
    /**
     * Get page count downloaded by spider.
     *
     * @return total downloaded page count
     */
    public long getPageCount() {
        return pageCount.get();
    }
    private void initComponent() {
        if (downloader == null) {
            this.downloader = new JsoupDownloader();
        }
        if (pipelines.isEmpty()) {
            pipelines.add(new ConsolePipeline());
        }
        if (threadPool == null || threadPool.isShutdown()) {
            if (executorService != null && !executorService.isShutdown()) {
                threadPool = new CountableThreadPool(threadNum, executorService);
            } else {
                threadPool = new CountableThreadPool(threadNum);
            }
        }
        startTime = new Date();
    }

    @Override
    public void run() {
        try {
            checkRunningStat();
            initComponent();
            while (!Thread.currentThread().isInterrupted() && stat == Status.Running) {
                Request request = scheduler.poll();
                if (request == null) {
                    if (threadPool.getThreadAlive() == 0) {
                        request = scheduler.poll(); // must has these check, or else cause a bug: if after poll a null, the old request process just done and submit a new request, but the new request will be never processed.
                    }else{ // has request processing.
                        ThreadHelper.sleep(50);
                        continue;
                    }
                }
                if(request == null){
                    break;
                }
                final Request requestFinal = request;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        ResultItems page = null;
                        try {
                            page = processRequest(requestFinal);
                        } catch (Exception e) {
                            onError(requestFinal);
                            LOG.error("Failed to process request " + requestFinal, e);
                        }
                        if (page != null) {
                            if (page.needSwitchProxy()) {
                                NetworkHelper.switchProxy();
                            }
                            if (page.isCacheUsed()) { // if not use cache, then no need to sleep
                                return;
                            }
                        }
                        ThreadHelper.sleep(sleepMillisecond);
                    }
                });
            }
            close();
        } catch (Throwable e) {
            LOG.error(null, e);
        }
        stat = Status.Stopped;
    }
    public void start() {
        Thread thread = new Thread(this);
        // 当所有的非守护线程结束时，程序也就终止了，同时会杀死进程中的所有守护线程。反过来说，只要任何非守护线程还在运行，程序就不会终止。
        thread.setDaemon(false);
        thread.start();
    }
    public void stop() {
        stat = Status.Stopped;
        LOG.info("Spider " + getUUID() + " stop success!");
    }
    /**
     * Process specific urls without url discovering.
     *
     * @param requests requests to process
     */
    public Spider test(Request... requests) throws IOException {
        initComponent();
        for (Request request : requests) {
            processRequest(request);
        }
        return this;
    }
    private void close(){
        for(Pipeline pipeline: pipelines){
            destroyEach(pipeline);
        }
        threadPool.shutdown();

    }
    private void destroyEach(Object object) {
        if (object instanceof Closeable) {
            try {
                ((Closeable) object).close();
            } catch (IOException e) {
                LOG.error(null, e);
            }
        }
    }
    private ResultItems processRequest(Request request) throws IOException {
        ResultItems page;
        if(request.getDownloader() == null){
            page = downloader.download(request, this);
        }else{
            page = request.getDownloader().download(request, this);
        }
        if(page == null){
            onError(request);
            return page;
        }
        if(request.getPageProcessor() == null) {
            pageProcessor.process(page);
        }else{
            request.getPageProcessor().process(page);
        }
        addRequests(page);
        doPipeline(page);
        pageCount.incrementAndGet();
        return page;
    }

    private void doPipeline(ResultItems page) {
        for (Pipeline pipeline : pipelines) {
            try {
                pipeline.process(page);
            } catch (IOException e) {
                LOG.error(null, e);
            }
        }
    }
    private void onError(Request request){
        scheduler.push(request);
    }
    private Spider addRequests(ResultItems page) {
        for (Request request : page.getTargetRequests()) {
            addRequest(request);
        }
        return this;
    }
    public Spider addRequest(Request request){
        scheduler.push(request);
        return this;
    }
    public Spider addPipeline(Pipeline pipeline){
        this.pipelines.add(pipeline);
        return this;
    }
}
