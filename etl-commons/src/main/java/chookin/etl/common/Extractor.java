package chookin.etl.common;

import chookin.utils.configuration.ConfigManager;
import chookin.utils.io.FileHelper;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by zhuyin on 7/6/14.
 *
 */
public class Extractor {
    private final static Logger LOG = Logger.getLogger(Extractor.class);
    private String url;
    protected String localFileName = null;
    private static String basePath = ConfigManager.getProperty("webpage.download.directory");
    private Document doc =null;
    private long validateSeconds ;

    /**
     * Get document from internet.
     * @param url
     */
    public Extractor(String url){
        this(url, 0L);
    }

    /**
     * If validateSeconds > 0, then get document from internet or from local disk.
     * @param url
     * @param validateSeconds the valid period of the downloaded file for this url. If local file expired, will download again. Unit is second.
     */
    public Extractor(String url, long validateSeconds){
        if(url == null){
            throw new NullArgumentException("url");
        }
        this.url = url;
        this.validateSeconds = validateSeconds;
    }

    public String getUrl(){return this.url;}

    public String getFileName(){
        if(this.localFileName == null ){
            localFileName = String.format("%s/%s", basePath, FileHelper.getUrlFileName(this.url));
        }
        return this.localFileName;
    }
    public boolean isAlreadySaved(){
        if(validateSeconds == 0L){
            return false;
        }
        File file = new File(this.getFileName());
        if(file.exists()){
            long time = file.lastModified();
            long now = new Date().getTime();
            if(now - time < validateSeconds * 1000){ // this file is still new
                return true;
            }
        }
        return false;
    }
    /**
     * Get the html document identified by this url.
     * When the html document got and validateSeconds is bigger than 0L, saving it to local disk.
     * @throws java.io.IOException
     */
    public Document getDocument() throws IOException {
        if(doc != null){
            return doc;
        }
        LOG.trace("get document " + this.getUrl());
        if(isAlreadySaved()){
            return doc = Jsoup.parse(new File(getFileName()), "utf-8", this.getUrl());
        }
        return doc = LinkHelper.getDocument(this.getUrl());
    }

    public void saveDocument() throws IOException {
        if(this.isAlreadySaved()){
            return;
        }
        FileHelper.save(getDocument().toString(), this.getFileName());
    }
    /**
     * Download the web resource to local disk without parsing if not been downloaded.
     * @param minsize ignore the resource if its' byte length less than minsize
     *
     * @return null when error in fetching remote resource
     * @throws IOException
     */
    public boolean saveAsResource(int minsize) throws IOException {
        if(isAlreadySaved()){
            return false;
        }
        byte[] bytes = LinkHelper.getDocumentBytes(this.getUrl());
        if(bytes.length < minsize){
            return false;
        }
        FileHelper.save(bytes, this.getFileName());
        return true;
    }
}
