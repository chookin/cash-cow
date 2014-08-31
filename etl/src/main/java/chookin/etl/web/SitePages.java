package chookin.etl.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import chookin.etl.common.*;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chookin.utils.Pair;

import chookin.etl.common.ResourceHelper;
import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;
import chookin.etl.web.site.Site;

public class SitePages extends Site {
	private static final Logger LOG = Logger.getLogger(SitePages.class);
	private String tableName;
	private String relBasePath;
	private ConcurrentSkipListSet<Link> urls = new ConcurrentSkipListSet<Link>();

	/**
	 * @param startAbsUrl
	 *            the URL we entering this site
	 * @throws MalformedURLException
	 */
	public SitePages(String domain, String startAbsUrl) {
		super(domain, startAbsUrl);
		this.tableName = getTableName();
		this.relBasePath = getRelativeBasePath();
	}

	private void checkFileSystem(FileSystem fs) throws SQLException {
		if (fs instanceof Database) {
			Database db = (Database) fs;
			if (!db.existsTable(tableName)) {
				createTable(db, tableName);
			}
		} else if (fs instanceof LocalDir) {
			LocalDir dir = (LocalDir) fs;
			String path = dir.getPath();
			if (!this.relBasePath.isEmpty()) {
				path = path + "/" + this.relBasePath;
			}
			if (new File(path).mkdirs()) {
				LOG.info(String.format("create directory %s", path));
			}
		}
	}

	/**
	 * @param maxTraversalDepth
	 *            if 0, download all the pages of this domain
	 * @param fs
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public SitePages downloadSitePages(int maxTraversalDepth, FileSystem fs)
			throws IOException, SQLException {
		Validate.isTrue(maxTraversalDepth >= 0,
				"para maxTraversalDepth should be not less than 0");
		if (maxTraversalDepth == 0) {
			maxTraversalDepth = Integer.MAX_VALUE;
		}
		checkFileSystem(fs);
		this.urls.clear();
		List<String> archived = this.getArchivedPagesUrl(fs);
		if (!archived.isEmpty()) {
			LOG.info("we have downloaded these pages:");
			for (String item : archived) {
				LOG.info(item);
				this.urls.add(new Link().setHref(item));
			}
			LOG.info("==END==");
		}
		Link link = new Link().setHref(this.getStartUrl());
		link.toString();
		if (this.urls.add(link)) {
			this.savePage(this.getStartUrl(), fs);
		}

		Collection<String> myUrls = new ArrayList<String>();
		myUrls.add(this.getStartUrl());
		int curDepth = 1;
		while (curDepth <= maxTraversalDepth && !myUrls.isEmpty()) {
			myUrls = getSitePagesWidthFirst(myUrls, curDepth,
					maxTraversalDepth, fs);
			++curDepth;
		}
		LOG.info(String.format("successed to download domain %s",
				this.getDomain()));
		return this;
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
	 * @throws SQLException
	 */
	private Collection<String> getSitePagesWidthFirst(Collection<String> urls,
			int curDepth, int maxTraversalDepth, FileSystem fs)
			throws IOException, SQLException {
		if (curDepth > maxTraversalDepth) {
			return new ArrayList<String>();
		}
		Collection<String> newUrls = new ArrayList<String>();
		for (String item : urls) {
			List<Link> myLinks = LinkHelper.getDocumentLinks(item);
			for (Link subItem : myLinks) {
				if (subItem.getHref().endsWith(".aspx")) {
					continue;// note: ignore *.aspx files
				}
				if (isMyUrl(subItem.getHref())) {
					if (this.urls.add(subItem)) {
						newUrls.add(subItem.getHref());
						this.savePage(subItem.getHref(), fs);
					}
				}
			}
		}
		return newUrls;
	}

	private static boolean rewrite = false;

	public SitePages savePage(String url, FileSystem fs) throws SQLException,
			IOException {
		LOG.info(String.format("downloading page %s", url));
		if (!rewrite && pageExists(fs, url)) {
			return this;
		}
		Document doc = LinkHelper.getDocument(url);
		return this.save(url, doc, fs);
	}

	private String getPageLocalDirPath(LocalDir dir, String url) {
		// parse the URL to get the relative path of the doc
		url = url.toLowerCase();

		int startIndex = url.indexOf(this.getDomain());
		if (startIndex == -1) {
			throw new IllegalArgumentException(
					String.format("url %s doesn't belong to domain %s", url,
							this.getDomain()));
		}
		startIndex += this.getDomain().length();
		int endIndex = url.lastIndexOf("/");
		if (endIndex < startIndex) {
			return String.format("%s/%s/index.html", dir.getPath(),
					this.relBasePath);
		} else {
			return String.format("%s/%s", dir.getPath(),
					LinkHelper.getUrlWithoutProtocolAndStart3W(url));
		}
	}

	private boolean pageExists(FileSystem fs, String url) {
		if (fs instanceof LocalDir) {
			LocalDir dir = (LocalDir) fs;
			File file = new File(getPageLocalDirPath(dir, url));
			if (file.isDirectory()) {
				return false;
			}
			return file.exists();
		}
		return false;
	}

	/**
	 * the node uri is not equals its url
	 * 
	 * @param url
	 *            HTML page URL
	 * @param doc
	 *            HTML page document
	 * @param fs
	 *            the file system which the document save to
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private SitePages save(String url, Document doc, FileSystem fs)
			throws SQLException, IOException {
		if (fs instanceof Database) {
			return this.save(url, doc, (Database) fs);
		}
		if (fs instanceof LocalDir) {
			return this.save(url, doc, (LocalDir) fs);
		}
		return this;
	}

	private SitePages save(String url, Document doc, Database db)
			throws SQLException {
		// String html = doc.toString();
		// Statement statement = db.getConnection().createStatement();
		// String sql = String.format(
		// "insert into %s(url, content) values(\"%s\", \"%s\")",
		// tableName, doc.baseUri(),
		// Entities.escape(html, new OutputSettings()));
		// LOG.info(sql);
		// statement.execute(sql);
		PreparedStatement ps = db.getConnection().prepareStatement(
				String.format(
						"insert into %s(url, title, content) values(?,?,?)",
						tableName));
		ps.setString(1, url);
		ps.setString(2, doc.title());
		ps.setString(3, doc.toString());// note: not require string escape
		ps.executeUpdate();
		return this;
	}

	/**
	 * @param url
	 * @param doc
	 * @param dir
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the URL doesn't belong to this site
	 */
	private SitePages save(String url, Document doc, LocalDir dir)
			throws IOException {
		LOG.info("transforming...");
		url = url.toLowerCase();

		Document myDoc = doc.clone();
		String regex = this.getDomain().replace(".", "\\.");
		regex = String.format("^((http|https)://)?(www\\.)?(%s)", regex);
		String subDomain = this.getSubDomain();
		if (!subDomain.isEmpty()) {
			regex = String.format("(%s)|(^/%s)", regex, subDomain);
		}
		regex = "(?i)" + regex;// The (?i) is placed at the beginning of the
								// pattern to enable case-insensitivity.
		regex = String.format("a[href~=%s]", regex);
		Elements elems = myDoc.select(regex);
		for (Element elem : elems) {
			String href = this.getOffLineUrl(url, elem.absUrl("href"));
			elem.attr("href", href);
		}

		LOG.info("achiving...");
		String fileName = getPageLocalDirPath(dir, url);
		Elements myCss = myDoc.select("link");
		for (Element item : myCss) {
			String src = item.absUrl("href");
			if (src.isEmpty()) {
				continue;
			}
			int indexVersion = src.indexOf('?');
			if (indexVersion != -1) {
				src = src.substring(0, indexVersion);
			}
			File file = this.saveResource(src, dir);
			if (file != null) {
				fileName = fileName.replace("\\", "/");
				String relative = ResourceHelper.getRelativePath(
                        fileName.substring(0, fileName.lastIndexOf('/')),// 取文件路径
                        file.getPath());
				item.attr("href", relative);
			}
		}
		Elements myJs = myDoc.select("script, img");
		for (Element item : myJs) {
			String src = item.absUrl("src");
			if (src.isEmpty()) {
				continue;
			}
			int indexVersion = src.indexOf('?');
			if (indexVersion != -1) {
				src = src.substring(0, indexVersion);
			}
			File file = this.saveResource(src, dir);
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

		this.saveDocument(myDoc, fileName);
		return this;
	}

	private SitePages saveDocument(Document doc, String fileName)
			throws IOException {
		if (new File(fileName).isDirectory()) {
			String newname = fileName + "/index.html";
			LOG.warn(String.format("rename page name from %s to %s", fileName,
					newname));
			fileName = newname;
		} else {
			String directory = fileName.substring(0, fileName.lastIndexOf('/'));
			ResourceHelper.mkdirs(directory);
		}

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

	/**
	 * download the web resource to local file without parse
	 * 
	 * @param resourceUrl
	 *            absolute URL where the resource locates at.
	 * @param dir
	 *            the base directory
	 * @param overwrite
	 *            whether rewrite when exists a same name resource
	 * @return return null when error in fetching remote resource
	 * @throws IOException
	 */
	private File saveResource(String resourceUrl, LocalDir dir)
			throws IOException {
		String domain = LinkHelper.getDomain(resourceUrl);
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
		LOG.info(String.format("download file %s", filepath));
		return file;
	}

	public Pair<String, String> loadPage(Database db, int id)
			throws SQLException {
		Statement statement = db.getConnection().createStatement();
		String sql = String.format("select * from %s where id = %d", tableName,
				id);
		ResultSet rs = statement.executeQuery(sql);
		try {
			if (rs.next()) {
				return new Pair<String, String>(rs.getString("url"),
						rs.getString("content"));
			}
			return null;
		} finally {
			rs.close();
		}
	}

	/**
	 * get resources we have download
	 * 
	 * @param fs
	 * @return if fs instance of db, we retrieve the downloads; if fs instance
	 *         of local directory, we return empty collection.
	 * @throws SQLException
	 */
	public List<String> getArchivedPagesUrl(FileSystem fs) throws SQLException {
		List<String> urls = new ArrayList<String>();
		if (fs instanceof Database) {
			Database db = (Database) fs;
			Statement statement = db.getConnection().createStatement();
			String sql = String.format("select url from %s", tableName);
			ResultSet rs = statement.executeQuery(sql);
			try {
				while (rs.next()) {
					urls.add(rs.getString("url"));
				}
			} finally {
				rs.close();
			}
		} else if (fs instanceof LocalDir) {
			// TODO
			return urls;
		}
		return urls;
	}

	private String getTableName() {
		String url = getDomain();
		int indexSlash = url.indexOf('/');
		if (indexSlash != -1) {
			url = url.substring(0, indexSlash);
		}
		int lastDotIndex = url.lastIndexOf(".");
		if (lastDotIndex == -1) {
			throw new IllegalArgumentException(String.format(
					"%s is invalid absolute url", url));
		}
		int prevDotIndex = url.substring(0, lastDotIndex).lastIndexOf(".");
		String coreUrl;
		if (prevDotIndex == -1) {
			coreUrl = url;
		} else {
			coreUrl = url.substring(prevDotIndex + 1);
		}
		coreUrl = coreUrl.replace('.', '_');
		return coreUrl;
	}

	private String getRelativeBasePath() {
		return this.getDomain();
	}

	private void createTable(Database db, String tableName) throws SQLException {
		Statement statement = db.getConnection().createStatement();
		String sql = String
				.format("create table %s(id int not null primary key AUTO_INCREMENT, url varchar(1024) not null, title varchar(256), tags varchar(256), abstract varchar(1024), content longtext, hashCode int)",
						tableName);
		statement.execute(sql);
	}
}
