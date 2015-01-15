package chookin.etl.web.site;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import chookin.etl.common.Link;

public class SiteUrls extends Site{
	private static final Logger LOG = Logger.getLogger(SiteUrls.class);
	private SiteCrawler crawler;
	private ConcurrentHashMap<Link, String> urls = new ConcurrentHashMap<Link, String>();
	public SiteUrls(String domain, String startAbsUrl)
			throws MalformedURLException {
		super(domain, startAbsUrl);
		this.crawler = new SiteCrawler(domain, startAbsUrl);
	}

	/**
	 * after you have retrieved site URLs, now you can get the retrieved URLs
	 * 
	 * @return
	 */
	public ConcurrentHashMap<Link, String> getRetrievedUrls() {
		return new ConcurrentHashMap<Link, String>(this.urls);
	}

	/**
	 * retrieve all the page URLs of this site in depth-first traversal,
	 * starting from the start URL.
	 * 
	 * @return
	 * @throws IOException
	 */
	public SiteUrls retrieveSiteUrls(int maxTraversalDepth) throws IOException {
		this.crawler.setMaxTraversalDepth(maxTraversalDepth);
		Thread thCrawler = new Thread(this.crawler);
		this.urls.clear();
		thCrawler.start();
		while (true) {
			try {
				java.lang.Thread.sleep(500);
			} catch (InterruptedException e) {
				LOG.warn(null, e);
			}
			boolean isCrawlDone = this.crawler.isCrawlDone();
			Map<Link, Document> docs = this.fetchDocuments();
			if (isCrawlDone && docs.isEmpty()) {
				break;
			}
			for (Entry<Link, Document> entry : docs.entrySet()) {
				Link link = entry.getKey();
				if(this.urls.containsKey(link)){
					continue;
				}
				this.urls.put(link, entry.getValue().title());
			}
		}
		LOG.info(String.format("successed to download domain %s",
				crawler.getDomain()));
		return this;
	}

	private Map<Link, Document> fetchDocuments() {
		return this.crawler.fetchCachedDocs();
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("start URL: ").append(this.getStartUrl()).append("\n");

		ConcurrentHashMap<Link, String> myUrls = this.getRetrievedUrls();
		for (Entry<Link, String> item : myUrls.entrySet()) {
			strb.append(item.getKey()).append(item.getValue()).append("\n");
		}
		return strb.toString();
	}
}
