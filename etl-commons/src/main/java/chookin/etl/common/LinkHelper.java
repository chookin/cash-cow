package chookin.etl.common;

import chookin.etl.downloader.JsoupDownloader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuyin on 3/2/15.
 */
public class LinkHelper {
    /**
     * parse HTML links from a doc file.
     *
     * @param in
     * @param charsetName
     * @param baseUrl
     * @return
     * @throws java.io.IOException
     */
    public static List<Link> parse(File in, String charsetName, String baseUrl)
            throws IOException {
        Document doc;
        if (baseUrl == null || baseUrl.isEmpty()) {
            doc = Jsoup.parse(in, charsetName);
        } else {
            doc = Jsoup.parse(in, charsetName, baseUrl);
        }
        return getDocumentLinks(doc);
    }

    /**
     *
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static List<Link> getDocumentLinks(String url) throws IOException {
        if (!UrlHelper.isHtmlPage(url)) {
            return new ArrayList<>();
        }
        Document doc = new JsoupDownloader().getDocument(url);
        return getDocumentLinks(doc);
    }


    /**
     * get all the links of a HTML web page
     *
     * @param doc
     *            HTML document
     * @return
     */
    public static List<Link> getDocumentLinks(Element doc) {
        Elements aes = doc.select("a");
        String baseUrl = doc.baseUri();
        List<Link> links = new ArrayList<Link>();
        for (Element item : aes) {
            String href = item.absUrl("href");
            if (href.isEmpty()
                    || UrlHelper.isHrefEqualIgnoreCase(baseUrl, href)) {
                continue;
            }
            if (href.contains("?")) {
                continue;
            }
            String title = item.attr("title");
            String text = item.text();
            links.add(new Link().setHref(href).setText(text).setTitle(title));
        }
        return links;
    }
}
