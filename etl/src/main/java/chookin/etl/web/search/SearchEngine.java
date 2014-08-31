package chookin.etl.web.search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.helper.Validate;

import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;

public abstract class SearchEngine {
	private static final Logger LOG = Logger.getLogger(SearchEngine.class);
	protected String host = "google.com";
	protected String protocol = "http";
	protected String agent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US)";
	protected int timeOut = 900000;

	protected int timeOutMaxTry = 50;
	protected int timeOutSleep = 1000;

	public SearchEngine(String host) {
		Validate.notEmpty(host);
		this.host = LinkHelper.getUrlWithoutProtocolAndStart3W(host);
	}

	public SearchEngine setProtocol(String protocol) {
		Validate.notEmpty(protocol);
		this.protocol = protocol;
		return this;
	}

	public String getProtocol() {
		return this.protocol;
	}
    public SearchEngine setHost(String host){
        this.host = host;
        return this;
    }
	public String getHost() {
		return this.host;
	}

	/**
	 * Encodes a string of arguments as a URL for a search query.
	 * 
	 * @param keyword
	 *            the argument pass to Google's search engine.
	 * 
	 * @return A URL for a search query based on the arguments.
	 */
	public abstract URL encodeQuery(String keyword);

	public abstract Map<String, Link> queryHosts(String keyword, int pageDepth)
			throws IOException;

	/**
	 * Downloads the contents of a URL as a String. This method alters the
	 * User-Agent of the HTTP request header so that Google does not return
	 * Error 403 Forbidden.
	 * 
	 * @param url
	 *            The URL to download.
	 * 
	 * @return The content downloaded from the URL as a string.
	 * 
	 * @throws IOException
	 *             Thrown if there is an error downloading the content.
	 */
	String downloadString(final URL url) throws IOException {
		LOG.info(String.format("dowload %s", url));
		final URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent", agent);
		connection.setConnectTimeout(timeOut);
		final InputStream stream = connection.getInputStream();
		return downloadString(stream);
	}

	/**
	 * Reads all contents from an input stream and returns a string from the
	 * data.
	 * 
	 * @param stream
	 *            The input stream to read.
	 * 
	 * @return A string built from the contents of the input stream.
	 * 
	 * @throws IOException
	 *             Thrown if there is an error reading the stream.
	 */
	static String downloadString(final InputStream stream) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch;
		while (-1 != (ch = stream.read()))
			out.write(ch);
		return out.toString();
	}

	@Override
	public String toString() {
		return this.protocol + "://" + this.host;
	}
	public static void output(Map<String, Link> links){
		for(Link link: links.values()){
			System.out.println(link);
		}
	}
}
