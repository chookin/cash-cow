package chookin.etl.web.site;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;

import chookin.etl.web.SitePages;
import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;

/**
 * crawl all the web pages of a web site
 * 
 * @author zhy
 * 
 */
public class SiteCrawler extends Site implements Runnable {
	private static final Logger LOG = Logger.getLogger(SitePages.class);
	private Set<Link> links = new HashSet<Link>();
	private ConcurrentHashMap<Link, Document> cachedDocs = new ConcurrentHashMap<Link, Document>();

	private int maxTraversalDepth = 9;

	private boolean isCrawlDone = true;

	private boolean quit = false;

	/**
	 * @param startAbsUrl
	 *            the URL we entering this site
	 * @throws MalformedURLException
	 */
	public SiteCrawler(String domain, String startAbsUrl) {
		super(domain, startAbsUrl);
	}

	public int getMaxTraversalDepth() {
		return this.maxTraversalDepth;
	}

	/**
	 * @param depth
	 *            if 0, then set maxTraversalDepth=Integer.MAX_VALUE, which
	 *            means crawler all the pages of this domain
	 * @return this
	 * @throws IllegalArgumentException
	 *             if depth < 0
	 */
	public SiteCrawler setMaxTraversalDepth(int depth) {
		Validate.isTrue(maxTraversalDepth >= 0,
				"para maxTraversalDepth should be not less than 0");
		if (depth == 0) {
			maxTraversalDepth = Integer.MAX_VALUE;
		} else {
			this.maxTraversalDepth = depth;
		}
		return this;
	}

	@Override
	public void run() {
		if (!this.isCrawlDone) {
			LOG.warn("crawlering");
			return;
		}
		this.isCrawlDone = false;
		this.quit = false;
		while (!isCrawlDone) {
			try {
				this.traverse();
			} catch (Throwable e) {
				LOG.error(null, e);
			}
		}
	}

	public SiteCrawler stop() {
		if (this.quit) {
			LOG.warn("not running");
		}
		this.quit = true;
		LOG.info("stop...");
		return this;
	}

	public boolean isCrawlDone() {
		return this.isCrawlDone;
	}

	public Map<Link, Document> fetchCachedDocs() {
		synchronized (this.cachedDocs) {
			Map<Link, Document> myCached = new TreeMap<Link, Document>(this.cachedDocs);
			this.cachedDocs.clear();
			return myCached;
		}
	}

	private SiteCrawler traverse() throws IOException {
		this.links.clear();
		synchronized (this.cachedDocs) {
			this.cachedDocs.clear();
		}
		Collection<String> myUrls = new ArrayList<String>();
		myUrls.add(this.getStartUrl());
		int curDepth = 1;
		while (curDepth <= maxTraversalDepth && !myUrls.isEmpty() && !quit) {
			LOG.info(String.format("traverse %d level", curDepth));
			myUrls = traverseSitePagesWidthFirst(myUrls, curDepth);
			++curDepth;
		}
		this.isCrawlDone = true;
		LOG.info(String.format("finish crawlering domain %s", this.getDomain()));
		return this;
	}

	private int traverseSleep = 50;
	/**
	 * get all the URLs of a site.<br>
	 * traversal in depth-first and recursively invoking to get all the URLs of
	 * this site.
	 * 
	 * @param startAbsUrl
	 *            start URL of the site
	 * @param curDepth
	 * @param maxTraversalDepth
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void getSiteURLsDepthFirst(String startAbsUrl, int curDepth,
			int maxTraversalDepth) throws IOException {
		if (curDepth > maxTraversalDepth) {
			return;
		}
		List<Link> myLinks = LinkHelper.getDocumentLinks(startAbsUrl);
		List<String> newUrls = new ArrayList<String>();
		for (Link item : myLinks) {
			if (isMyUrl(item.getHref())) {
				if (this.links.add(item))
					newUrls.add(item.getHref());
			}
		}
		LOG.info(startAbsUrl + " " + newUrls.size());
		for (String item : newUrls) {
			getSiteURLsDepthFirst(item, curDepth + 1, maxTraversalDepth);
		}
	}
	/**
	 * @param urls
	 *            URLs traverse from
	 * @param curDepth
	 *            current traverse depth, depth from 1
	 * @param maxTraversalDepth
	 *            max traverse depth
	 * @return the new turn URLs that will be traversed from
	 * @throws IOException
	 */
	private Collection<String> traverseSitePagesWidthFirst(
			Collection<String> urls, int curDepth) {
		if (curDepth > maxTraversalDepth) {
			return new ArrayList<String>();
		}
		Collection<String> newUrls = new ArrayList<String>();
		Set<Link> errorLinks = new HashSet<Link>();
		for (String item : urls) {
			if (quit) {
				break;
			}
			if (!LinkHelper.isHtmlPage(item)) {
				continue;
			}
			Link link = new Link().setHref(item);
			try {
				newUrls.addAll(traverseUrl(link));
			} catch (IOException e) {
				errorLinks.add(link);
				LOG.warn(null, e);
			}
		}
		while (!errorLinks.isEmpty()) {
			for (Link link : errorLinks) {
				if (quit) {
					break;
				}
				try {
					newUrls.addAll(traverseUrl(link));
					errorLinks.remove(link);
				} catch (IOException e) {
					LOG.warn(null, e);
					try {
						java.lang.Thread.sleep(2000);
					} catch (InterruptedException e1) {
						LOG.error(null, e);
					}
				}
			}
		}
		return newUrls;
	}

	private Collection<String> traverseUrl(Link link) throws IOException {
		Collection<String> newUrls = new ArrayList<String>();
		if (quit) {
			return newUrls;
		}
		if (this.traverseSleep > 0) {
			try {
				Thread.sleep(this.traverseSleep);
			} catch (InterruptedException e) {
				LOG.error(null, e);
			}
		}
		LOG.info(String.format("get %s", link));
		Document doc = null;
		doc = LinkHelper.getDocument(link.getHref());
		this.cacheDocument(link, doc);

		List<Link> myLinks = LinkHelper.getDocumentLinks(doc);
		for (Link subItem : myLinks) {
			if (ignoreDocument(subItem)) {
				continue;
			}
			if (isMyUrl(subItem.getHref())) {
				if (!this.links.add(subItem)) {
					continue;
				}
				newUrls.add(subItem.getHref());
			}
		}
		return newUrls;
	}

	private int cacheSize = 100;

	private SiteCrawler cacheDocument(Link link, Document doc) {
		boolean isBufferFull = false;
		while (true) {
			synchronized (this.cachedDocs) {
				if (this.cachedDocs.size() > this.cacheSize) {
					isBufferFull = true;
				} else {
					this.cachedDocs.put(new Link().setHref(link.getHref().toLowerCase()), doc);
					break;
				}
			}
			if (isBufferFull) {
				try {
					LOG.warn("site crawer's cache is full");
					java.lang.Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOG.warn(null, e);
				}
			}
		}
		return this;
	}

	private static boolean ignoreDocument(Link link) {
		for (String ext : excludeDocumentExtensions) {
			if (link.getHref().endsWith(ext)) {
				return true;
			}
		}
		return false;
	}

	private static String[] excludeDocumentExtensions = { ".aspx" };
}
