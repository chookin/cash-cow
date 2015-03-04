package chookin.etl.downloader;

import chookin.etl.Spider;
import chookin.etl.common.Request;
import chookin.etl.common.ResultItems;

import java.io.IOException;

/**
 * Get document from internet.
 *
 * Created by zhuyin on 3/2/15.
 */
public interface Downloader {
    ResultItems download(Request request, Spider spider) throws IOException;
}
