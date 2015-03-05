package chookin.etl.downloader;

import chookin.etl.Spider;
import chookin.etl.common.Request;
import chookin.etl.common.UrlHelper;

import java.io.File;
import java.util.Date;

/**
 * Created by zhuyin on 3/2/15.
 */
public class Cache {
    private Request request;
    private Spider spider;
    protected String localFileName = null;
    public Request getRequest(){
        return request;
    }
    public Spider getSpider(){
        return spider;
    }
    public Cache(Request request, Spider spider){
        this.request = request;
        this.spider = spider;
    }
    /**
     * Can use cached resource?
     * @return true if can use cache.
     */
    public boolean usable(){
        if(getSpider().getValidateMilliSeconds() <= 0L){
            return false;
        }
        File file = new File(this.getFileName());
        if(file.exists()){
            long time = file.lastModified();
            long now = new Date().getTime();
            if(now - time < getSpider().getValidateMilliSeconds()){ // this file is still new
                return true;
            }
        }
        return false;
    }
    public String getFileName(){
        if(this.localFileName == null ){
            localFileName = UrlHelper.getFilePath(this.getRequest().getUrl());
        }
        return this.localFileName;
    }

}
