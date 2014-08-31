package chookin.etl.web.site;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chookin.etl.common.ResourceHelper;
import org.jsoup.helper.Validate;

import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;

public class Site {
	private String domain;
	private String startUrl;

	public Site(String domain, String startAbsUrl) {
		Validate.notNull(startAbsUrl);
		this.startUrl = startAbsUrl;
		if (domain == null || domain.isEmpty()) {
			this.domain = LinkHelper.getDomain(startAbsUrl);
		} else {
			this.domain = LinkHelper.getDomain(domain);
		}
	}

	public String getStartUrl() {
		return this.startUrl;
	}

	/**
	 * a site domain contains no protocol, for example, kaixinguo.so,
	 * kaixinguo.so/jsoup
	 * 
	 * @return this site domain
	 */
	public String getDomain() {
		return this.domain;
	}

	/**
	 * @return the one level domain, for example, the base domain of
	 *         kaixinguo.so/jsoup is kaixinguo.so
	 */
	public String getBaseDomain() {
		int indexSlash = this.domain.indexOf("/");
		if (indexSlash == -1) {
			return this.domain;
		} else {
			return this.domain.substring(0, indexSlash);
		}
	}

	public String getOffLineUrl(String curPageUrl, String linkedUrl) {
		if (linkedUrl.contains("?")) {
			return linkedUrl;
		}
		curPageUrl = LinkHelper.getUrlWithoutProtocolAndStart3W(curPageUrl).toLowerCase();
		linkedUrl = LinkHelper.getUrlWithoutProtocolAndStart3W(linkedUrl).toLowerCase();

		if (LinkHelper.getExtension(curPageUrl).isEmpty()) {
			curPageUrl = curPageUrl + ".html";
		}
		if (LinkHelper.getExtension(linkedUrl).isEmpty()) {
			linkedUrl = linkedUrl + ".html";
		}
		String path = ResourceHelper
				.getRelativePath(
                        curPageUrl.substring(0, curPageUrl.lastIndexOf('/')),
                        linkedUrl);
		return path;
	}

	protected String getSubDomain() {
		int indexSlash = this.domain.indexOf("/");
		if (indexSlash == -1) {
			return "";
		} else {
			return this.domain.substring(indexSlash + 1);
		}
	}

	/**
	 * select the URLs that is in this site.
	 * 
	 * @param links
	 * @return
	 * @throws MalformedURLException
	 */
	public List<Link> getMyUrls(Collection<Link> links)
			throws MalformedURLException {
		List<Link> rst = new ArrayList<Link>();
		for (Link item : links) {
			if (isMyUrl(item.getHref())) {
				rst.add(item);
			}
		}
		return rst;
	}

	/**
	 * judge whether the URL is in this site.
	 * 
	 * @param url
	 *            the URL we judge
	 * @return true if this URL indicating a page of this site
	 */
	public boolean isMyUrl(String url) {
		String myUrl = url.toLowerCase();
		myUrl = LinkHelper.getUrlWithoutProtocolAndStart3W(myUrl);
		return myUrl.startsWith(this.domain);
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("domain: ").append(this.domain);
		if (!this.startUrl.equals(this.domain)) {
			strb.append("\n").append("start url: ").append(this.startUrl);
		}
		return strb.toString();
	}
}
