package chookin.etl.web.site;

import java.io.IOException;
import java.util.*;

import chookin.etl.common.Link;
import org.apache.log4j.Logger;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;


public abstract class Saver {
	private static final Logger LOG = Logger.getLogger(Saver.class);
	public static boolean rewrite = false;
	private SiteCrawler crawler;
	protected Set<Link> crawledLinks = new HashSet<>();
	protected Map<Link, Document> errorLinks = new HashMap<>();

	public Saver(SiteCrawler crawler) {
		Validate.notNull(crawler);
		this.crawler = crawler;
	}

	public Site getSite() {
		return this.crawler;
	}

	/**
	 * @param maxTraversalDepth
	 *            if 0, download all the pages of this domain
	 * @return
	 * @throws IOException
	 */
	public Saver saveSitePages(int maxTraversalDepth) throws IOException {
		this.crawler.setMaxTraversalDepth(maxTraversalDepth);
		Thread thCrawler = new Thread(this.crawler);
		thCrawler.start();
		checkFileSystem();
		this.crawledLinks.clear();
		this.errorLinks.clear();
		List<String> archived = this.getArchivedPagesUrl();
		if (!archived.isEmpty()) {
			LOG.info("we have downloaded these pages:");
			for (String item : archived) {
				LOG.info(item);
				this.crawledLinks.add(new Link().setHref(item));
			}
			LOG.info("==END==");
		}
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
			for (Map.Entry<Link, Document> entry : docs.entrySet()) {
				Link link = entry.getKey();
				if (this.crawledLinks.add(link) || rewrite) {
					LOG.info(String.format("save page: %s", link.getHref()));
					try {
						this.save(link.getHref(), entry.getValue());
					} catch (IOException e) {
						this.errorLinks.put(link, entry.getValue());
						LOG.error(null, e);
					}
				}
			}
		}
		while (!this.errorLinks.isEmpty()) {
			LOG.info("resave pages that happendd errors when download");
			for (Map.Entry<Link, Document> entry : this.errorLinks.entrySet()) {
				LOG.info(String.format("save page: %s", entry.getKey()
						.getHref()));
				try {
					this.save(entry.getKey().getHref(), entry.getValue());
					this.errorLinks.remove(entry.getKey());
				} catch (IOException e) {
					LOG.error(null, e);
					try {
						java.lang.Thread.sleep(1000);
					} catch (InterruptedException e1) {
						LOG.error(null, e);
					}
				}
			}
		}
		LOG.info(String.format("successed to download domain %s",
				crawler.getDomain()));
		return this;
	}

	protected abstract void checkFileSystem() throws IOException;

	public abstract List<String> getArchivedPagesUrl() throws IOException;

	public abstract Saver save(String url, Document doc) throws IOException;

	private Map<Link, Document> fetchDocuments() {
		return this.crawler.fetchCachedDocs();
	}
}
