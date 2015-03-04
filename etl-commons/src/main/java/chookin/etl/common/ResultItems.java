package chookin.etl.common;

import chookin.etl.Spider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuyin on 3/2/15.
 */
public class ResultItems {
    public ResultItems(Request request, Spider spider){
        this.request = request;
        this.spider = spider;
    }
    Request request;
    Spider spider;
    Object resource;
    /**
     * Whether or not the document is loaded from cache.
     */
    boolean cacheUsed = false;

    /**
     * Whether or not to skip pipeline processing.
     */
    boolean skip = false;

    /**
     * Whether or not need to switch proxy.
     */
    boolean needSwitchProxy = false;

    /**
     * Requests that need to
     */
    private List<Request> targetRequests = new ArrayList<Request>();
    private Map<String, Object> fields = new LinkedHashMap<String, Object>();
    public Request getRequest(){
        return request;
    }

    public Spider getSpider(){
        return spider;
    }
    public ResultItems setResource(Object resource){
        this.resource = resource;
        return this;
    }
    public Object getResource(){
        return resource;
    }
    public ResultItems cacheUsed(boolean cacheUsed){
        this.cacheUsed = cacheUsed;
        return this;
    }
    public boolean isCacheUsed(){
        return cacheUsed;
    }

    public ResultItems skip(boolean skip){
        this.skip = skip;
        return this;
    }

    public boolean isSkip(){
        return this.skip;
    }

    public ResultItems needSwitchProxy(boolean needSwitchProxy){
        this.needSwitchProxy = needSwitchProxy;
        return this;
    }

    public boolean needSwitchProxy(){
        return this.needSwitchProxy;
    }

    public List<Request> getTargetRequests() {
        synchronized (targetRequests) {
            return new ArrayList<>(targetRequests);
        }
    }
    public ResultItems addTargetRequest(Request request) {
        synchronized (targetRequests) {
            targetRequests.add(request);
        }
        return this;
    }
    /**
     * add requests to fetch
     *
     * @param requests
     */
    public ResultItems addTargetRequest(List<Request> requests) {
        synchronized (targetRequests) {
            targetRequests.addAll(requests);
        }
        return this;
    }

    public Object getField(String key){
        return fields.get(key);
    }
    public Map<String, Object> getAll() {
        return fields;
    }
    public ResultItems setField(String key, Object value) {
        fields.put(key, value);
        return this;
    }
}
