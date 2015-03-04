package chookin.etl.common;

import chookin.utils.Validate;
import chookin.utils.configuration.ConfigManager;
import chookin.utils.io.FileHelper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 *
 */
public class UrlHelper {
	private static String basePath = new File(ConfigManager.getProperty("page.download.directory", "/tmp")).getAbsolutePath();
	private static String[] documentExtensions = { "", ".htm", ".html" };
	private static boolean ignorePound = true;

	private static String protocolRegex = "[a-zA-Z]+://";


	private UrlHelper() {
	}

	public static String getProtocolRegex() {
		return protocolRegex;
	}
	/**
	 * erase the protocol of a URL and return the erased.
	 *
	 * @param url
	 * @return
	 */
	public static String eraseProtocol(String url) {
		if(url == null){
			return null;
		}
		return url.replaceAll(String.format("(?i)^%s", UrlHelper.getProtocolRegex()), "");
	}
	public static String eraseProtocolAndStart3W(String url) {
		if(url == null){
			return null;
		}
		// (?i)让表达式忽略大小写进行匹配;
		// '^'和'$'分别匹配字符串的开始和结束
		return eraseProtocol(url).replaceAll("(?i)(^www.)", "");
	}

	/**
	 * erase the end "/" and inner page jump.
	 * @param url
	 * @return
	 */
	public static String trimUrl(String url) {
		Validate.notNull(url);
		String myUrl = StringUtils.strip(url);
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

	/**
	 * Is equals after url trim.
	 */
	public static boolean isUrlEqual(String lhs, String rhs) {
		String myLhs = UrlHelper.trimUrl(lhs);
		String myRhs = UrlHelper.trimUrl(rhs);
		return myLhs.compareTo(myRhs) == 0;
	}

	public static boolean isHrefEqualIgnoreCase(String lhs, String rhs) {
		String myLhs = UrlHelper.trimUrl(lhs);
		String myRhs = UrlHelper.trimUrl(rhs);
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
	 * judge whether resource indicated by the URL is a HTML page by the URL's
	 * extension
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
		if(url == null){
			return null;
		}
		String myUrl = eraseProtocolAndStart3W(url).toLowerCase();
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
		if(url == null){
			return null;
		}
		String myUrl = eraseProtocolAndStart3W(url).toLowerCase();
		int lastLeftSlashIndex = myUrl.lastIndexOf('/');
		if (lastLeftSlashIndex == -1) {
			return myUrl;
		}
		return myUrl.substring(0, lastLeftSlashIndex);
	}
	public static String getBaseDomain(String url) {
		if(url == null){
			return null;
		}
		String myUrl = eraseProtocolAndStart3W(url).toLowerCase();
		int index = myUrl.indexOf('/');
		if (index == -1) {
			return myUrl;
		}
		return myUrl.substring(0, index);
	}


	private static String getFileName(String url) {
		String filename = UrlHelper.eraseProtocolAndStart3W(url);
		filename = FileHelper.formatFileName(filename);
		filename = FilenameUtils.normalizeNoEndSeparator(filename);
		if(FilenameUtils.getExtension(filename).isEmpty()){
			filename = filename + ".html";
		}
		return filename;
	}
	public static String getFilePath(String url){
		return String.format("%s/%s", basePath, getFileName(url));
	}
}
