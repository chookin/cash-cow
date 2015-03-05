package chookin.etl.downloader;

import chookin.etl.Spider;
import chookin.etl.common.Request;
import chookin.etl.common.ResultItems;
import chookin.etl.common.UrlHelper;
import chookin.utils.configuration.ConfigManager;
import chookin.utils.io.FileHelper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/3/15.
 */
public class CasperJsDownloader implements Downloader {
    private static final Logger LOG = Logger.getLogger(CasperJsDownloader.class);
    private String getOut(String url){
        return UrlHelper.getFilePath(url);
    }
    private String script = "js/downloader.js";
    public Downloader setScript(String script){
        this.script = script;
        return this;
    }
    /**
     * Execute system processes with Java ProcessBuilder and Process.
     * http://alvinalexander.com/java/java-exec-processbuilder-process-1
     * @return downloader process.
     * @throws IOException
     */
    private Process getDownloaderProcess(String url, String userAgent) throws IOException {
        List<String> command = new ArrayList<>();
        command.add("casperjs");
        command.add(getJsPath(this.script));
        command.add(String.format("--url=%s", url));
        command.add(String.format("--out=%s", getOut(url)));
        command.add(String.format("--userAgent=%s", userAgent));

        boolean enableProxy = ConfigManager.getPropertyAsBool("proxy.enable");
        if(enableProxy){
            String host=ConfigManager.getProperty("proxy.host");
            String port = ConfigManager.getProperty("proxy.port");
            command.add(String.format("--proxy=%s:%s", host, port));
        }
        LOG.trace("exec cmd: " + command + " for download "+url);
        return new ProcessBuilder(command).start();
    }

    protected void doDownload(Request request, Spider spider) throws IOException {
        String url = request.getUrl();
        String out = getOut(url);
        FileHelper.makeParentDirs(out);
        Process proc = getDownloaderProcess(url, spider.getUserAgent());
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        long start = System.currentTimeMillis();
        boolean isTimeOut = false;
        String s;
        while ((s = stdInput.readLine()) != null) {
            LOG.debug(s);
            if(System.currentTimeMillis() > start + spider.getTimeOut()){
                isTimeOut = true;
                break;
            }
        }
        if(isTimeOut){
            LOG.warn("time out to download " + url);
        }else {
            LOG.trace("success to download " + url);
        }
        proc.destroy();
    }
    /**
     *
     * @param jsFile js file name
     * @return
     * @throws java.io.IOException
     */
    public String getJsPath(String jsFile) throws IOException {
        String path = jsFile;
        File file = new File(path);
        path = file.getAbsolutePath();
        if(file.exists()){
            return path;
        }else{
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsFile);
            FileHelper.save(in, path);
            return path;
        }
    }
    public Document getDocument(String url, String userAgent) throws IOException {
        return (Document) download(
                new Request(url),
                new Spider()
                        .setUserAgent(userAgent)
        ).getResource();
    }

    @Override
    public ResultItems download(Request request, Spider spider) throws IOException {
        String url = request.getUrl();
        LOG.trace("get " + url);

        ResultItems resultItems = new ResultItems(request, spider);
        Cache cache = new Cache(request, spider);
        if(!request.isIgnoreCache() && cache.usable()){
            resultItems.cacheUsed(true);
        }else{
            doDownload(request, spider);
        }
        Document doc = Jsoup.parse(new File(cache.getFileName()), "utf-8", url);
        resultItems.setResource(doc);
        return resultItems;
    }
}
