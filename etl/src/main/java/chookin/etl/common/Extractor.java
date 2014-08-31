package chookin.etl.common;

import chookin.etl.web.jsoup.LinkHelper;
import chookin.stock.Configuration;
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
        this.localPath = localPath;
    }
    public String getUrl(){return this.url;}

    public String getLocalPath() {return this.localPath;}

    public String getFileName(){
        if(this.localPath != null && this.localFileName != null){
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
     * When get the html document, saving it to local disk.
     * @param existedValidPeriod the valid period of the downloaded file for this url. If local file expired, will download again. Unit is millisecond.
     * @return
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
        return doc;
    }

    public void save(Document doc) throws IOException {
        if(this.localPath == null){
            throw new NullArgumentException("localPath");
        }
        FileHelper.save(doc.toString(), this.getFileName());
    }
}
