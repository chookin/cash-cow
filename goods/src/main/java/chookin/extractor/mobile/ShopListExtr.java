package chookin.extractor.mobile;

import chookin.etl.common.Extractor;
import chookin.orm.ShopEntity;
import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhuyin on 1/6/15.
 */
public abstract class ShopListExtr {
    private final static Logger LOG = Logger.getLogger(ShopListExtr.class);
    protected static long webpageValidPeriod = ConfigManager.getPropertyAsLong("shoplist.webpage.validPeriod");
    public abstract List<ShopEntity> extract() throws IOException;
    private Document doc = null;
    private String startUrl = null;
    public ShopListExtr(String startUrl){
        this.startUrl = startUrl;
    }
    public Document getDocument() throws IOException {
        if(doc == null){
            return getDocument(startUrl);
        }
        return doc;
    }
    public Document getDocument(String url) throws IOException {
        Extractor extractor = new Extractor(url);
        Document myDoc = extractor.getDocument(webpageValidPeriod);
        if(webpageValidPeriod > 0L) {
            extractor.saveDocument();
        }
        return myDoc;
    }
}
