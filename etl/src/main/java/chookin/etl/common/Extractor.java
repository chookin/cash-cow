package chookin.etl.common;

import chookin.etl.web.jsoup.LinkHelper;
import chookin.utils.io.FileHelper;
import chookin.utils.web.UrlHelper;
import org.apache.commons.lang.NullArgumentException;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by chookin on 7/6/14.
 *
 * If not saved in local disk, then download it.
 */
public class Extractor {
    private final static Logger LOG = Logger.getLogger(Extractor.class);
    private String url;
    protected String localFileName = null;
    protected String localPath;
    private static String basePath = null;

    public static String getBasePath() {
        return basePath;
    }

    public static void setBasePath(String basePath) {
        if(basePath == null || basePath.trim().isEmpty()){
            throw new IllegalArgumentException("basePath");
        }
        Extractor.basePath = basePath;
    }

    public Extractor(String url){
        if(url == null){
            throw new NullArgumentException("url");
        }
        this.url = url;
        String username = System.getProperty("user.name");
        if(basePath == null){
            this.localPath = String.format("/home/%s/stock", username);
        }else {
            this.localPath = basePath;
        }
    }

    public String getUrl(){return this.url;}

    public String getLocalPath() {return this.localPath;}

    public String getFileName(){
        if(this.localFileName == null ){
            String url = UrlHelper.eraseProtocolAndStart3W(this.url);
            String filename = String.format("%s/%s", this.getLocalPath(), url);
            filename = FileHelper.formatFileName(filename);
            if(FileHelper.getExtension(filename).isEmpty()){
                filename = filename + ".html";
            }
            this.localFileName = filename;
        }
        return this.localFileName;
    }

    /**
     * get the html document identified by this.url.
     * When the html document got and existedValidPeriod is bigger than 0L, saving it to local disk.
     * @param existedValidPeriod the valid period of the downloaded file for this url. If local file expired, will download again. Unit is millisecond.
     * @throws java.io.IOException
     */
    public Document getDocument(long existedValidPeriod) throws IOException {
        LOG.info("get document " + this.getUrl());
        File file = new File(this.getFileName());
        if(file.exists()){
            long time = file.lastModified();
            long now = new Date().getTime();
            if(now - time < existedValidPeriod){ // this file is still new
                return Jsoup.parse(file, "utf-8");
            }
        }
        Document doc = LinkHelper.getDocument(this.getUrl());
        if(existedValidPeriod > 0L){
            FileHelper.save(doc.toString(), this.getFileName());
        }
        return doc;
    }
    public Document getDocument() throws IOException{
        return this.getDocument(0L);
    }
    /**
     * download the web resource to local disk without parsing if not been downloaded.
     * @param minsize ignore the resource if its' byte length less than minsize
     *
     * @return return null when error in fetching remote resource
     * @throws IOException
     */
    public void saveAsResource(int minsize) throws IOException {
        File file = new File(this.getFileName());
        if (file.exists()) {
            return;
        }
        byte[] bytes;
        while(true){
            try {
                bytes = LinkHelper.getDocumentBytes(this.getUrl());
                break;
            } catch (HttpStatusException e) {
                if(e.getStatusCode() == 503){
                    LOG.warn(null, e);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                throw new IOException(e);
            }
        }
        if(bytes.length < minsize){
            return;
        }
        FileHelper.save(bytes, this.getFileName());
    }
}
