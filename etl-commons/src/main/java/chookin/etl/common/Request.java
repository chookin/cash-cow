package chookin.etl.common;

import chookin.etl.downloader.Downloader;
import chookin.etl.processor.PageProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Object contains url to crawl.<br>
 * It contains some additional information.<br>
 * Created by zhuyin on 3/2/15.
 */
public class Request {
    public Request(String url) {
        if(url == null || url.trim().isEmpty()){
            throw new IllegalArgumentException("Must supply a valid URL");
        }
        this.url = url;
    }
    /**
     * Url of request web page.
     */
    private String url;
    /**
     * Store additional information in extras.
     */
    private Map<String, Object> extras;

    /**
     * Count of request schedule retry.
     */
    private int retryCount = 0;

    /**
     * Type of target resource.
     */
    TargetResource target = TargetResource.Document;

    /**
     * Force to not use cache.
     */
    private boolean ignoreCache = false;


    /**
     * Its custom page downloader.
     */
    private Downloader downloader;

    /**
     * Its custom page processor.
     */
    private PageProcessor pageProcessor;

    public Downloader getDownloader() {
        return downloader;
    }

    public Request setDownloader(Downloader downloader) {
        this.downloader = downloader;
        return this;
    }
    public Request setPageProcessor(PageProcessor pageProcessor){
        this.pageProcessor = pageProcessor;
        return this;
    }

    public PageProcessor getPageProcessor(){
        return this.pageProcessor;
    }
    public Request ignoreCache(boolean ignoreCache){
        this.ignoreCache = ignoreCache;
        return this;
    }
    public boolean isIgnoreCache(){
        return ignoreCache;
    }
    public String getUrl() {
        return url;
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request setExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<>();
        }
        extras.put(key, value);
        return this;
    }

    public int getRetryCount(){
        return retryCount;
    }

    public Request incrRetryCount(){
        ++retryCount;
        return this;
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", extras=" + extras +
                ", ignoreCache=" + ignoreCache +
                '}';
    }

    public Request setTarget(TargetResource target){
        this.target = target;
        return this;
    }
    public TargetResource getTarget(){
        return this.target;
    }
    @Override
    public int hashCode() {
        return url.hashCode();
    }

    public static enum TargetResource{
        Document,
        String,
        ByteArray
    }

}
