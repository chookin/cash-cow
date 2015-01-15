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
 * Created by chookin on 7/6/14.
 *
 * If not saved in local disk, then download it.
 */
public class Extractor {
    private final static Logger LOG = Logger.getLogger(Extractor.class);
    private String url;
    protected String localFileName = null;
    private static String basePath = ConfigManager.getProperty("webpage.download.directory");
    private Document doc;

    public Extractor(String url){
        if(url == null){
            throw new NullArgumentException("url");
        }
        this.url = url;
    }

    public String getUrl(){return this.url;}

    public String getFileName(){
        if(this.localFileName == null ){
            localFileName = String.format("%s/%s", basePath, FileHelper.getUrlFileName(this.url));
        }
        return this.localFileName;
    }

    /**
     * get the html document identified by this.url.
     * When the html document got and validateSeconds is bigger than 0L, saving it to local disk.
     * @param validateSeconds the valid period of the downloaded file for this url. If local file expired, will download again. Unit is second.
     * @throws java.io.IOException
     */
    public Document getDocument(long validateSeconds) throws IOException {
        LOG.trace("get document " + this.getUrl());
        if(validateSeconds > 0L){
            File file = new File(this.getFileName());
            if(file.exists()){
                long time = file.lastModified();
                long now = new Date().getTime();
                if(now - time < validateSeconds * 1000){ // this file is still new
                    return doc = Jsoup.parse(file, "utf-8", this.getUrl());
                }
            }
        }
        return doc = LinkHelper.getDocument(this.getUrl());
    }
    public Document getDocument() throws IOException{
        return this.getDocument(0L);
    }
    public void saveDocument() throws IOException {
        if(doc == null){
            getDocument();
        }
        FileHelper.save(doc.toString(), this.getFileName());
    }
    /**
     * download the web resource to local disk without parsing if not been downloaded.
     * @param minsize ignore the resource if its' byte length less than minsize
     *
     * @return return null when error in fetching remote resource
     * @throws IOException
     */
    public boolean saveAsResource(int minsize) throws IOException {
        File file = new File(this.getFileName());
        if (file.exists()) {
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
