package chookin.etl.web.site;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import chookin.utils.Pair;

import chookin.etl.common.Database;

public class DbSaver extends Saver {
	private static final Logger LOG = Logger.getLogger(DbSaver.class);
	private Database db;
	private String tableName;
	public DbSaver(SiteCrawler site) {
		super(site);
		this.tableName = getTableName();
	}

	@Override
	protected void checkFileSystem() throws IOException {
			try {
				if (!db.existsTable(tableName)) {
					createTable(db, tableName);
					LOG.info(String.format("create table %s", tableName));
				}
			} catch (SQLException e) {
				throw new IOException(String.format(
						"failed to create table %s", tableName), e);
			}
	}

	public String getTableName() {
		String url = getSite().getDomain();
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
	public List<String> getArchivedPagesUrl() throws IOException {
		List<String> urls = new ArrayList<String>();
		Statement statement;
		try {
			statement = db.getConnection().createStatement();

			String sql = String.format("select url from %s", tableName);
			ResultSet rs = statement.executeQuery(sql);
			try {
				while (rs.next()) {
					urls.add(rs.getString("url"));
				}
			} finally {
				rs.close();
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
		return urls;
	}

	public Saver save(String url, Document doc) throws IOException {
		// String html = doc.toString();
		// Statement statement = db.getConnection().createStatement();
		// String sql = String.format(
		// "insert into %s(url, content) values(\"%s\", \"%s\")",
		// tableName, doc.baseUri(),
		// Entities.escape(html, new OutputSettings()));
		// LOG.info(sql);
		// statement.execute(sql);
		PreparedStatement ps;
		try {
			ps = db.getConnection()
					.prepareStatement(
							String.format(
									"insert into %s(url, title, content) values(?,?,?)",
									tableName));
			ps.setString(1, url);
			ps.setString(2, doc.title());
			ps.setString(3, doc.toString());// note: not require string escape
			ps.executeUpdate();
			return this;
		} catch (SQLException e) {
			throw new IOException(String.format("faild to save page: %s", url),
					e);
		}

	}

	private void createTable(Database db, String tableName) throws SQLException {
		Statement statement = db.getConnection().createStatement();
		String sql = String
				.format("create table %s(id int not null primary key AUTO_INCREMENT, url varchar(1024) not null, title varchar(256), tags varchar(256), abstract varchar(1024), content longtext, hashCode int)",
						tableName);
		statement.execute(sql);
	}
}
