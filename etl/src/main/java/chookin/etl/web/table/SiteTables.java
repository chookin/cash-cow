package chookin.etl.web.table;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import chookin.etl.web.data.Link;
import chookin.etl.web.site.SiteCrawler;

public class SiteTables {
	private static final Logger LOG = Logger.getLogger(SiteTables.class);
	private SiteCrawler crawler;
	private Map<Link, Collection<Table>> tables = new HashMap<Link, Collection<Table>>();

	public SiteTables(String domain, String startAbsUrl) {
		this.crawler = new SiteCrawler(domain, startAbsUrl);
	}

	/**
	 * retrieve all the page tables of this site in depth-first traversal,
	 * starting from the start URL.
	 * 
	 * @return
	 * @throws IOException
	 */
	public SiteTables retrieveSiteTables(int maxTraversalDepth)
			throws IOException {
		this.crawler.setMaxTraversalDepth(maxTraversalDepth);
		Thread thCrawler = new Thread(this.crawler);
		this.tables.clear();
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
				if (this.tables.containsKey(link)) {
					continue;
				}
				this.tables.put(link, TableExtract.fromElement(
						entry.getValue(), entry.getValue().title()));
			}
		}
		LOG.info(String.format("successed to extract tables of domain %s",
				crawler.getDomain()));
		return this;
	}

	private Map<Link, Document> fetchDocuments() {
		return this.crawler.fetchCachedDocs();
	}
}
