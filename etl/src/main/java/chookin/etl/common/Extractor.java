package chookin.etl.common;

import chookin.etl.web.jsoup.LinkHelper;
import chookin.utils.io.FileHelper;
import chookin.utils.web.UrlHelper;
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
    private String localFileName = null;
    private String localPath;

    public Extractor(String url){
        this(url, null);
    }
    public Extractor(String url, String localPath){
        if(url == null){
            throw new NullArgumentException("url");
        }
        this.url = url;
        if(localPath == null || localPath.isEmpty()){
            this.localPath = ".";
        }else{
            this.localPath = localPath;
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
        if(this.getFileName() != null){
            File file = new File(this.getFileName());
            if(file.exists()){
                long time = file.lastModified();
                long now = new Date().getTime();
                if(now - time < existedValidPeriod){ // this file is still new
                    return Jsoup.parse(file, "utf-8");
                }
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
}
