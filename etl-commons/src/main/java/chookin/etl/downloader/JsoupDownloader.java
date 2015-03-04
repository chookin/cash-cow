package chookin.etl.downloader;

import chookin.etl.Spider;
import chookin.etl.common.Request;
import chookin.etl.common.ResultItems;
import chookin.utils.io.FileHelper;
import chookin.utils.web.NetworkHelper;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by zhuyin on 3/2/15.
 */
public class JsoupDownloader implements Downloader {
    private final static Logger LOG = Logger.getLogger(JsoupDownloader.class);

    public Document getDocument(String url) throws IOException{
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
        return getDocument(url, userAgent);
    }
    public Document getDocument(String url, String userAgent) throws IOException {
        return (Document) download(
                new Request().setUrl(url),
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
            switch (request.getTarget()){
                case String:
                    String body = FileHelper.readString(cache.getFileName());
                    return resultItems.setResource(body).cacheUsed(true);
                case ByteArray:
                    // TODO better process resource download of png, pdf...
                    return resultItems.cacheUsed(true);
                default:
                    Document doc = Jsoup.parse(new File(cache.getFileName()), "utf-8", url);
                    return resultItems.setResource(doc).cacheUsed(true);
            }
        }

        try {
            switch (request.getTarget()){
                case String:
                    return resultItems.setResource(getResponse(request, spider).body());
                case ByteArray:
                    return resultItems.setResource(getResponse(request, spider).bodyAsBytes());
                default:
                    return resultItems.setResource(getResponse(request, spider).parse());
            }
        } catch (MalformedURLException e) {
            throw new IOException(e.toString() + " " + url, e);
        } catch (  UnknownHostException | SocketException | SocketTimeoutException e){
            LOG.error(e.toString() + " on get " + url);
        } catch(HttpStatusException e) {
            switch (e.getStatusCode()){
                case 404: // 请求的网页不存在
                case 500: // a problem when extract yhd, such as http://item.yhd.com/item/43583907, no use even many retry.
                    throw new IOException(e.toString() + " " + url, e);
                default:
                    LOG.error("http status code " + e.getStatusCode() + " on get " + url);
                    NetworkHelper.switchProxy();
            }
        }
        return resultItems.needSwitchProxy(true);
    }

    Connection.Response getResponse(Request request, Spider spider) throws IOException {
        // ignoreContentType(true) is set because otherwise Jsoup will throw an exception that the content is not HTML parseable -- that's OK in this
        // case because we're using bodyAsBytes() to get the response body,
        // rather than parsing.
        return Jsoup.connect(request.getUrl()).userAgent(spider.getUserAgent()).timeout(spider.getTimeOut()).ignoreContentType(true).execute();

    }
}
