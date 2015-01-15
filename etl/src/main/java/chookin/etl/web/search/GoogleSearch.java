package chookin.etl.web.search;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import chookin.etl.common.Link;
import chookin.etl.common.LinkHelper;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GoogleSearch extends SearchEngine {
	private static final Logger LOG = Logger.getLogger(GoogleSearch.class);

	public GoogleSearch() {
		super("google.ee");
	}

	public Map<String, Link> queryHosts(String keyword, int pageDepth)
			throws IOException {
		Validate.isTrue(pageDepth > 0);
		Map<String, Link> links = new HashMap<String, Link>();
		if (keyword == null || keyword.isEmpty()) {
			return links;
		}
		URL url = encodeQuery(keyword);
		Document doc = getDocument(url);
		int index = 0;
		do {
			links.putAll(getCiteHost(doc));
			doc = getNextDocument(doc, ++index);
		} while (doc != null && index < pageDepth);
		return links;
	}

	private String pageNavSelector = "table#nav";

	Document getNextDocument(Document curDoc, int curIndex) throws IOException {
		Elements elems = curDoc.select(pageNavSelector);
		if (elems.isEmpty()) {
			return null;
		}
		Elements tds = elems.first().select("a");
		String targetIndex = String.valueOf(curIndex + 1);
		for (Element item : tds) {
			if (item.text().equals(targetIndex)) {
				LOG.info(String.format("get page %d", curIndex));
				String href = item.attr("href");// note:
												// Element.absUrl("attr")在实际上有数据的情况下也可能会返回""
				URL url = new URL(this.getProtocol(), this.getHost(), href);
				return getDocument(url);
			}
		}
		return null;
	}

	Map<String, Link> getCiteHost(Document doc) throws IOException {
		Map<String, Link> links = new HashMap<String, Link>();
		Elements elems = doc.select("cite");
		for (Element elem : elems) {
			String url = elem.text();
			String host = LinkHelper.getHost(url);
			if (links.containsKey(host)) {
				Link link = links.get(host);
				link.setWeight(link.getWeight() + 1.0f);
			} else {
				links.put(host, new Link().setHref(host));
			}
		}
		return links;
	}

	Document getDocument(URL url) throws IOException {
		String html = null;
		int count = 0;
		while (count < timeOutMaxTry) {
			try {
				html = this.downloadString(url);
				break;
			} catch (java.net.ConnectException e) {
				++count;
				LOG.error(e + " " + url.toString());
				try {
					java.lang.Thread.sleep(timeOutSleep);
				} catch (InterruptedException e1) {
					LOG.error(e);
				}
			}
		}
		Document doc = Jsoup.parse(html);
		// System.out.println(html);
		// System.out.println(doc.toString());
		return doc;
	}

	@Override
	public URL encodeQuery(final String keyword) {
		try {
			final StringBuilder localAddress = new StringBuilder();
			localAddress.append("/search?q=");
			final String encoding = URLEncoder.encode(keyword, "UTF-8");
			localAddress.append(encoding);
			return new URL(this.getProtocol(), this.getHost(),
					localAddress.toString());

		} catch (final IOException e) {
			// Errors should not occur under normal circumstances.
			throw new RuntimeException(String.format(
					"An error occurred while encoding the query arguments %s.",
					keyword));
		}
	}
}
