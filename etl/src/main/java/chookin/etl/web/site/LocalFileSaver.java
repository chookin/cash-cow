package chookin.etl.web.site;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import chookin.etl.common.ResourceHelper;
import cmri.etl.common.UrlHelper;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chookin.etl.common.LocalDir;

public class LocalFileSaver extends Saver {
	public LocalFileSaver(SiteCrawler site, LocalDir dir) {
		super(site);
		Validate.notNull(dir);
		this.relBasePath = this.getRelativeBasePath();
		this.dir = dir;
	}

	private static final Logger LOG = Logger.getLogger(LocalFileSaver.class);
	private String relBasePath;
	private LocalDir dir;

	@Override
	protected void checkFileSystem() throws IOException {
		String path = dir.getPath();
		if (!this.relBasePath.isEmpty()) {
			path = path + "/" + this.relBasePath;
		}
		File file = new File(path);
		if (file.mkdirs()) {
			LOG.info(String.format("create directory %s", path));
		}
	}

	public boolean pageExists(String url) {
		File file = new File(getPageLocalDirPath(url));
		if (file.isDirectory()) {
			return false;
		}
		return file.exists();
	}

	/**
	 * @param url
	 * @param doc
	 * @param dir
	 * @return
	 * @throws IOException
	 */
	public Saver save(String url, Document doc) throws IOException {
		if (!rewrite && pageExists(url)) {
			return this;
		}
		LOG.info("transforming...");
		url = url.toLowerCase();
		Document myDoc = doc.clone();
		this.updateSiteOtherHrefs(myDoc, url);

		LOG.info("achiving...");
		String fileName = getPageLocalDirPath(url);
		this.saveHrefTypeResources(myDoc, fileName);
		this.saveSrcTypeResources(myDoc, fileName);
		this.saveDocument(myDoc, fileName);
		return this;
	}

	private Saver updateSiteOtherHrefs(Document doc, String url) {
		String regex = getSite().getDomain().replace(".", "\\.");
		regex = String.format("^((http|https)://)(www\\.)?(%s)", regex);
		// String subDomain = getSite().getSubDomain();
		// if (!subDomain.isEmpty()) {
		// regex = String.format("(%s)|(^/%s)", regex, subDomain);
		// }
		// "#" inner page jump, ignore it
		regex = String.format("(?i)%s|(^\\.\\./)|(^\\./)|(^(%s){0}[^#])",
				regex, UrlHelper.getProtocolRegex());// The (?i) is placed at
														// the
														// beginning of the
		// pattern to enable case-insensitivity.
		regex = String.format("a[href~=%s]", regex);
		Elements elems = doc.select(regex);
		for (Element elem : elems) {
			String href = getSite().getOffLineUrl(url, elem.absUrl("href"));
			if (href == null) {// url =
								// http://jade-jade.cheng.com/hpu/2012-spring/csci-2912,
								// elem = <a
								// href="mailto:ycheng@hpu.edu">ycheng@hpu.edu</a>
				continue;
			}
			elem.attr("href", href);
		}
		return this;
	}

	private Saver saveHrefTypeResources(Document doc, String fileName)
			throws IOException {
		Elements myCss = doc.select("link");
		for (Element item : myCss) {
			String src = item.absUrl("href");
			if (src.isEmpty()) {
				continue;
			}
			int indexVersion = src.indexOf('?');
			if (indexVersion != -1) {
				src = src.substring(0, indexVersion);
			}
			File file = this.saveResource(src);
			if (file != null) {
				fileName = fileName.replace("\\", "/");
				String relative = ResourceHelper.getRelativePath(
                        fileName.substring(0, fileName.lastIndexOf('/')),// 取文件路径
                        file.getPath());
				item.attr("href", relative);
			}
		}
		return this;
	}

	/**
	 * @param doc
	 *            maybe update doc's attributes
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private Saver saveSrcTypeResources(Document doc, String fileName)
			throws IOException {
		Elements myJs = doc.select("script, img");
		for (Element item : myJs) {
			String src = item.absUrl("src");
			if (src.isEmpty()) {
				continue;
			}
			int indexVersion = src.indexOf('?');
			if (indexVersion != -1) {
				src = src.substring(0, indexVersion);
			}
			File file = this.saveResource(src);
			// String relative = new FileHelper(fileName).toURI().relativize(new
			// FileHelper(file.getPath()).toURI()).getPath();
			// it's not work. for filename =
			// "d:/site_downloads/blog.csdn.net/trend_cdc_spn/index.html",
			// file.getPath()=
			// "d:/site_downloads/blog.csdn.net/trend_cdc_spn/resources/static.blog.csdn.net/scripts/jquery.js",
			// it returns
			// "file:/d:/site_downloads/blog.csdn.net/trend_cdc_spn/resources/static.blog.csdn.net/scripts/jquery.js"
			if (file != null) {
				fileName = fileName.replace("\\", "/");
				String relative = ResourceHelper.getRelativePath(
                        fileName.substring(0, fileName.lastIndexOf('/')),// 取文件路径
                        file.getPath());
				item.attr("src", relative);
			}
		}
		return this;
	}

	private Saver saveDocument(Document doc, String fileName)
			throws IOException {
		String directory = fileName.substring(0, fileName.lastIndexOf('/'));
		ResourceHelper.mkdirs(directory);

		FileWriter writer;
		try {
			writer = new FileWriter(fileName);
		} catch (FileNotFoundException e) {
			throw e;// note: can't create a file and a folder with the same name
					// and in the same folder. The OS would not allow
					// you to do that since the name is the id for that
					// file/folder object. So we have delete the older file
		}
		writer.write(doc.toString());
		writer.close();
		return this;
	}

	public String getRelativeBasePath() {
		return getSite().getDomain();
	}

	public List<String> getArchivedPagesUrl() throws IOException {
		List<String> urls = new ArrayList<String>();
		// TODO
		return urls;
	}

	/**
	 * download the web resource to local file without parse
	 * 
	 * @param resourceUrl
	 *            absolute URL where the resource locates at.
	 * @return return null when error in fetching remote resource
	 * @throws IOException
	 */
	private File saveResource(String resourceUrl) throws IOException {
		String domain = UrlHelper.getBaseDomain(resourceUrl);
		int indexLastSlash = resourceUrl.lastIndexOf("/");
		if (indexLastSlash == -1) {
			throw new IllegalArgumentException(String.format(
					"%s is not a valid resource URL", resourceUrl));
		}
		String dirname = String.format("%s/%s/resources/%s", dir.getPath(),
				this.relBasePath, domain);
		String filepath = String.format("%s/%s", dirname,
				resourceUrl.substring(indexLastSlash));
		File file = new File(filepath);
		if (!rewrite && file.exists()) {
			return file;
		}
		new File(dirname).mkdirs();
		byte[] bytes = null;
		try {
			bytes = LinkHelper.getDocumentBytes(resourceUrl);
		} catch (HttpStatusException e) {
			LOG.error(null, e);
			return null;
		}
		if (bytes == null) {
			return null;
		}
		FileOutputStream output = new FileOutputStream(file);
		output.write(bytes);
		output.close();
		LOG.info(String.format("download file %s", filepath.replace('\\', '/')));
		return file;
	}

	public String getPageLocalDirPath(String url) {
		// parse the URL to get the relative path of the doc
		url = url.toLowerCase();

		int startIndex = url.indexOf(getSite().getDomain());
		if (startIndex == -1) {
			throw new IllegalArgumentException(String.format(
					"url %s doesn't belong to domain %s", url, getSite()
							.getDomain()));
		}
		startIndex += getSite().getDomain().length();
		int endIndex = url.lastIndexOf("/");
		if (endIndex < startIndex) {// happens when this URL is the domain name,
									// but without ended "/"
			return String.format("%s/%s.html", dir.getPath(), this.relBasePath);
		} else {
			if (LinkHelper.getExtension(url).isEmpty()) {
				return String.format("%s/%s.html", dir.getPath(),
						UrlHelper.eraseProtocolAndStart3W(url));
			} else {
				return String.format("%s/%s", dir.getPath(),
						UrlHelper.eraseProtocolAndStart3W(url));
			}
		}
	}
}
