package chookin.etl.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import chookin.utils.Pair;

import chookin.etl.common.Database;
import chookin.etl.common.FileSystem;
import chookin.etl.common.LocalDir;
import chookin.etl.common.Link;
import chookin.etl.common.LinkHelper;
import chookin.etl.web.search.GoogleSearch;
import chookin.etl.web.search.SearchEngine;
import chookin.etl.web.site.LocalFileSaver;
import chookin.etl.web.site.Saver;
import chookin.etl.web.site.SiteCrawler;
import chookin.etl.web.site.SiteUrls;
import chookin.etl.web.table.Table;
import chookin.etl.web.table.TableExtract;

public class Test {
	private final static Logger LOG = Logger.getLogger(Test.class);
	private final static String RESOURCE_PATH="etl/src/main/resources/";

	public static void main(String[] args) {
        try {
//            extractTable();
//            testLinkExtract();
//            testTableExtract();
//            testSiteUrlExtract();
//            testLoadPage();
            testDownloadSite();
//            testLocalFileSaver();
//            testSiteSearch();
//            testGoogleSearch();
        } catch (Throwable t) {
			LOG.error(null, t);
		}

	}

	public static void testLinkExtract() throws IOException {
		String filePath = RESOURCE_PATH + "index.html";
		System.out.println("cur dir:" + System.getProperty("user.dir"));//user.dir指定了当前的路径
		File in = new File(filePath);
		String charsetName = "utf-8";
		String baseUrl = null;
		List<Link> links = LinkHelper.parse(in, charsetName, baseUrl);
		for (Link item : links) {
			System.out.println(item);
		}
	}

	public static void testSiteUrlExtract() throws IOException {
		String webSite = "http://www.goodfellow.com";
		Collection<Link> siteUrls = new SiteUrls(webSite, null).retrieveSiteUrls(0)
				.getRetrievedUrls().keySet();
		String hostname = LinkHelper.getDomain(webSite);
		FileWriter writer = new FileWriter(hostname + ".txt");
		CsvListWriter csv = new CsvListWriter(writer,
				CsvPreference.EXCEL_PREFERENCE);
		for (Link item : siteUrls) {
			csv.write(item);
		}
		System.out.println("done");
	}
    public  static void extractTable() throws IOException{
        String url;
        url = "http://vip.stock.finance.sina.com.cn/mkt/#sh_a";
        List<Table> tables = TableExtract.fromUrl(url);
        for (Table item : tables) {
            System.out.println(item.toCSV());
            // System.out.println(item);
        }
    }
	public static void testTableExtract() throws IOException {
		String url = "http://mobilereviews.net/details-for-Motorola%20L7.htm";

		url = "http://www.webelements.com/";
		url = "http://www.msiport.com/msi-eureka/buy-online/purchase/selectElements";
		url = "http://www.msiport.com/";
		url = "http://www.metallurgy.nist.gov/phase/solder/solder.html";
        url = "http://vip.stock.finance.sina.com.cn/mkt/#sh_a";
		List<Table> tables = TableExtract.fromUrl(url);
//		List<Table> tables = TableExtract.fromFile("g:/Users/zhy/Desktop/web chookin.etl/solder.html", null);
		for (Table item : tables) {
			System.out.println(item.toCSV());
			// System.out.println(item);
		}
	}

	public static void testDownloadSite() throws ClassNotFoundException,
			SQLException, IOException, InterruptedException {
		List<String> urls = new ArrayList<String>();
		urls.add("http://blog.csdn.net/v_JULY_v/");
		urls.add("http://100bbb.com/");
		urls.add("http://blog.csdn.net/trend_cdc_spn/");
		urls.add("http://jsoup.org/");
		FileSystem fs = new Database();
		fs = new LocalDir("d:/site_downloads");
		for (String url : urls) {
			while (true) {
				try {
					SitePages sitePages = new SitePages(null, url);
					fs.open();
					sitePages.downloadSitePages(1, fs);
					break;
				} catch (Exception e) {
					LOG.error(null, e);
					Thread.sleep(1000);
				} finally {
					fs.close();
				}
			}
		}
	}

	public static void testLoadPage() throws ClassNotFoundException,
			SQLException, IOException {
		String url = "http://blog.csdn.net/trend_cdc_spn";
		Database db = new Database();
		db.open();
		Pair<String, String> pair;
		try {
			SitePages sitePages = new SitePages(null, url);
			pair = sitePages.loadPage(db, 1);
			if (pair != null) {
				String myHtml = pair.getValue();
				String srcHtml = LinkHelper.getDocument(url).toString();
				if (myHtml.compareTo(srcHtml) == 0) {
					System.out.println("ok!");
				}
				System.out.println(pair.toString());
			}
		} finally {
			db.close();
		}
	}

	public static void testLoadEscapePage() throws ClassNotFoundException,
			SQLException, IOException {
		String url = "http://kaixinguo.so/";
		Database db = new Database();
		db.open();
		Pair<String, String> pair;
		try {
			SitePages sitePages = new SitePages(null, url);
			pair = sitePages.loadPage(db, 1);
			if (pair != null) {
				//String myHtml = Entities.unescape(pair.getValue());
                String myHtml = pair.getValue();
				String srcHtml = Jsoup.connect(url).timeout(300000).get()
						.toString();
				if (myHtml.compareTo(srcHtml) == 0) {
					System.out.println("ok!");
				}
				System.out.println(pair.toString());
			}
		} finally {
			db.close();
		}
	}

	public void testEscape(String filename, String charsetName)
			throws IOException {
		Document doc = Jsoup.parse(new File(filename), charsetName);
		String html = doc.toString();
		//String escapHtml = Entities.escape(html, new OutputSettings());
		//String reversHtml = Entities.unescape(escapHtml);
		//if (html.compareTo(reversHtml) == 0) {
		//	System.out.print("ok");
		//}
	}

	public static void testLocalFileSaver() {
		List<String> urls = new ArrayList<String>();
		// urls.add("http://blog.csdn.net/trend_cdc_spn/");
		// urls.add("http://blog.csdn.net/v_JULY_v/");
		// urls.add("http://www.jade-jade.cheng.com/uh/home/");
		urls.add("http://jade-jade.cheng.com/hpu/");
		String domain = "jade-jade.cheng.com";
		LocalDir dir = new LocalDir("d:/site_downloads");
		for (String url : urls) {
			while (true) {
				try {
					SiteCrawler crawler = new SiteCrawler(domain, url);
					Saver saver = new LocalFileSaver(crawler, dir);
					saver.saveSitePages(3);
					break;
				} catch (Exception e) {
					LOG.error(null, e);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						LOG.error(null, e1);
					}
				} finally {
				}
			}
		}
	}

	public static void testSiteSearch() throws IOException {
		SearchEngine search = new GoogleSearch();
        search.setHost("google.ee");
		search.setProtocol("http");
		// search = new BaiduSearch();
		String keyword = "java";
		Map<String, Link> links = search.queryHosts(keyword, 500);
		SearchEngine.output(links);

	}

	public static void testGoogleSearch() throws IOException {
		String[] keywords = { "赢享开心果" };
		jade.cheng.GoogleSearch search = new jade.cheng.GoogleSearch();
		search.search(keywords);
	}
}