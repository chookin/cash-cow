package chookin.etl.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;

import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;
import chookin.etl.web.site.Site;

public class SiteVirginPages extends Site {
	public SiteVirginPages(String domainName, String startAbsUrl)
			throws MalformedURLException {
		super(startAbsUrl, null);
	}

	public SiteVirginPages retieveVirginPages(int maxTraversalDepth)
			throws IOException {
		Validate.isTrue(maxTraversalDepth >= 0,
				"para maxTraversalDepth should be not less than 0");
		if (maxTraversalDepth == 0) {
			maxTraversalDepth = Integer.MAX_VALUE;
		}
		Link link = new Link().setHref(this.getStartUrl()).setTitle(
				LinkHelper.getPageTitle(this.getStartUrl()));
		Collection<String> myUrls = new ArrayList<String>();
		myUrls.add(this.getStartUrl());
		return this;
	}

	public List<Element> getVirginElements(int maxTraversalDepth)
			throws IOException {
		Validate.isTrue(maxTraversalDepth >= 0,
				"para maxTraversalDepth should be not less than 0");
		if (maxTraversalDepth == 0) {
			maxTraversalDepth = Integer.MAX_VALUE;
		}
		List<Element> rst = new ArrayList<Element>();
		Link link = new Link().setHref(this.getStartUrl()).setTitle(
				LinkHelper.getPageTitle(this.getStartUrl()));
		Collection<String> myUrls = new ArrayList<String>();
		myUrls.add(this.getStartUrl());

		return rst;
	}

	private static class ElementHash {
		private int elemCode;
		private Set<Integer> urlCodes;

		public ElementHash(int elemCode) {
			this.elemCode = elemCode;
		}

		public int getElemCode() {
			return this.elemCode;
		}

		public Set<Integer> getUrlCodes() {
			return this.urlCodes;
		}
	}

	private static class UrlHash {
		private int urlCode;
		private Set<String> urls = new TreeSet<String>();

		public int getUrlCode() {
			return this.urlCode;
		}

		public Set<String> getUrls() {
			return this.urls;
		}
	}

	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append("start URL: ").append(this.getStartUrl()).append("\n");
		return strb.toString();
	}
}
