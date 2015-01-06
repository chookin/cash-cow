package chookin.etl.web.jsoup;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import chookin.utils.configuration.ConfigManager;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chookin.etl.web.data.Link;

/**
 *
 */
public class LinkHelper {
	private LinkHelper() {
	}

	private static final Logger LOG = Logger.getLogger(LinkHelper.class);

	private static String[] documentExtensions = { "", ".htm", ".html" };
	private static boolean ignorePound = true;
	private static int timeOut = 60000;

	private static String protocolRegex = "[a-zA-Z]+://";

	public static String getProtocolRegex() {
		return protocolRegex;
	}


	public static String trimUrl(String url) {
		Validate.notNull(url);
		String myUrl = url.trim();
		if (myUrl.endsWith("/")) {
			myUrl = myUrl.substring(0, myUrl.length() - 1);
		}
		if (ignorePound) {
			myUrl = trimUrlPoundSuffix(myUrl);
		}
		if (myUrl.compareTo(url) == 0) {
			return myUrl;
		}
		return trimUrl(myUrl);
	}

	public static boolean isUrlEqual(String lhs, String rhs) {
		String myLhs = LinkHelper.trimUrl(lhs);
		String myRhs = LinkHelper.trimUrl(rhs);
		return myLhs.compareTo(myRhs) == 0;
	}

	public static boolean isHrefEqualIgnoreCase(String lhs, String rhs) {
		String myLhs = LinkHelper.trimUrl(lhs);
		String myRhs = LinkHelper.trimUrl(rhs);
		return myLhs.compareToIgnoreCase(myRhs) == 0;
	}

	/**
	 * "#" indicates inner page jump mostly, so may be you need to trim strings
	 * behind "#"
	 *
	 * @param url
	 * @return
	 */
	private static String trimUrlPoundSuffix(String url) {
		Validate.notNull(url);
		int indexPound = url.indexOf("#");
		if (indexPound == -1) {
			return url;
		}
		return trimUrl(url.substring(0, indexPound));
	}

	/**
	 * parse HTML links from a doc file.
	 *
	 * @param in
	 * @param charsetName
	 * @param baseUrl
	 * @return
	 * @throws IOException
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

	public static String getPageTitle(String url) throws IOException {
		return getDocument(url).title();
	}

	private static String userAgent = ConfigManager.getProperty("userAgent");

	/**
	 * retrieve html document by url
	 * @param url
	 * @return parsed document
	 * @throws IOException
	 */
	public static Document getDocument(String url) throws IOException {
		while (true) {
			try {
				return Jsoup.connect(url).userAgent(userAgent).timeout(timeOut).ignoreContentType(true).get();
			} catch (MalformedURLException e) {
				throw new IOException(e.getMessage() + " " + url, e);
			} catch (  UnknownHostException | SocketException | SocketTimeoutException e){
				LOG.warn(url, e);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					LOG.warn(null, e1);
				}
			} catch(HttpStatusException e) {
				switch (e.getStatusCode()){
					case 404: // 请求的网页不存在
						throw new IOException(e.getMessage() + " " + url, e);
					default:
						LOG.warn("http status code "+e.getStatusCode(), e);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							LOG.warn(null, e);
						}

				}
			}
		}
	}

	/**
	 * use Jsoup to fetch any URL and get the data as bytes, if you don't want
	 * to parse it as HTML
	 *
	 * @param url
	 * @return null when error.
	 * @throws IOException
	 */
	public static byte[] getDocumentBytes(String url) throws IOException {
		// ignoreContentType(true) is set because otherwise Jsoup will throw an exception that the content is not HTML parseable -- that's OK in this
		// case because we're using bodyAsBytes() to get the response body,
		// rather than parsing.
		while (true) {
			try {
				return Jsoup.connect(url).userAgent(userAgent).timeout(timeOut).ignoreContentType(true).execute().bodyAsBytes();
			} catch (MalformedURLException e) {
				throw new IOException(e.getMessage() + " " + url, e);
			} catch (  UnknownHostException | SocketException | SocketTimeoutException e){
				LOG.warn(url, e);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					LOG.error(null, e1);
				}
			} catch(HttpStatusException e) {
				switch (e.getStatusCode()){
					case 404: // 请求的网页不存在
						throw new IOException(e.getMessage() + " " + url, e);
					default:
						LOG.warn("http status code "+e.getStatusCode(), e);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							LOG.warn(null, e);
						}

				}
			}
		}
	}

	/**
	 * Get the body of the response as a plain string.
	 * @return body
	 */
	public static String getDocumentBody(String url) throws IOException {
		// ignoreContentType(true) is set because otherwise Jsoup will throw an exception that the content is not HTML parseable -- that's OK in this
		// case because we're using bodyAsBytes() to get the response body,
		// rather than parsing.
		while (true) {
			try {
				return Jsoup.connect(url).userAgent(userAgent).timeout(timeOut).ignoreContentType(true).execute().body();
			} catch (MalformedURLException e) {
				throw new IOException(e.getMessage() + " " + url, e);
			} catch ( UnknownHostException |  SocketException | SocketTimeoutException e){
				LOG.warn(url, e);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					LOG.error(null, e1);
				}
			} catch(HttpStatusException e) {
				switch (e.getStatusCode()){
					case 404: // 请求的网页不存在
						throw new IOException(e.getMessage() + " " + url, e);
					default:
						LOG.warn("http status code "+e.getStatusCode(), e);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							LOG.warn(null, e);
						}

				}
			}
		}
	}

	/**
	 *
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static List<Link> getDocumentLinks(String url) throws IOException {
		if (!isHtmlPage(url)) {
			return new ArrayList<Link>();
		}
		Document doc = getDocument(url);
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
					|| LinkHelper.isHrefEqualIgnoreCase(baseUrl, href)) {
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

	/**
	 * judge whether resource indicated by the URL is a HTML page by the URL's
	 * extension
	 *
	 * @param url
	 * @return
	 */
	public static boolean isHtmlPage(String url) {
		String extension = getExtension(url);
		for (String item : documentExtensions) {
			if (extension.compareTo(item) == 0) {
				return true;
			}
		}
		return false;

	}

	public static String getExtension(String url) {
		url = url.replace('\\', '/');
		int indexLastSlash = url.lastIndexOf('/');
		if (indexLastSlash == -1) {
			indexLastSlash = 0;
		}
		int indexLastDot = url.substring(indexLastSlash).lastIndexOf('.');
		if (indexLastDot == -1) {
			return "";
		} else {
			return url.substring(indexLastDot).toLowerCase();
		}
	}
	public static String getHost(String url){
		String myUrl = getUrlWithoutProtocolAndStart3W(url).toLowerCase();
		int firstSlashIndex = myUrl.indexOf('/');
		if(firstSlashIndex == -1){
			return myUrl;
		}
		return myUrl.substring(0, firstSlashIndex);
	}

	/**
	 * get the domain without protocol and resource name for a URL
	 *
	 * @param url
	 * @return a site domain contains no protocol
	 */
	public static String getDomain(String url) {
		String myUrl = getUrlWithoutProtocolAndStart3W(url).toLowerCase();
		int lastLeftSlashIndex = myUrl.lastIndexOf('/');
		if (lastLeftSlashIndex == -1) {
			return myUrl;
		}
		return myUrl.substring(0, lastLeftSlashIndex);
	}

	/**
	 * erase the protocol of a URL and return the erased.
	 *
	 * @param url
	 * @return
	 */
	public static String getUrlWithoutProtocol(String url) {
		return url.replaceAll(String.format("(?i)%s", LinkHelper.getProtocolRegex()), "");
	}
	public static String getUrlWithoutProtocolAndStart3W(String url) {
		// (?i)让表达式忽略大小写进行匹配;
		// '^'和'$'分别匹配字符串的开始和结束
		return getUrlWithoutProtocol(url).replaceAll("(?i)(^www.)", "");
	}
}
