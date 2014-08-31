package chookin.etl.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class Database implements FileSystem {
	public class DbConfiguration {
		private String url = "jdbc:mysql://localhost:3306/extractor";
		private String user = "root";
		private String passwd = "root";
	
		public String getUrl() {
			return this.url;
		}
	
		public String getUser() {
			return this.user;
		}
	
		public String getPwd() {
			return this.passwd;
		}
	}

	private static final Logger LOG = Logger.getLogger(Database.class);
	private Connection conn;

	public Connection getConnection() {
		return this.conn;
	}

	private DbConfiguration conf = new DbConfiguration();

	public boolean open() {
		if (this.conn == null) {
			try {
				this.openConnection(conf.getUrl(), conf.getUser(),
                        conf.getPwd());
				return true;
			} catch (ClassNotFoundException e) {
				LOG.error(null, e);
			} catch (SQLException e) {
				LOG.error(null, e);
			}
			return false;
		}
		return true;
	}

	public boolean close() {
		if (this.conn != null) {
			try {
				this.conn.close();
			} catch (SQLException e) {
				LOG.error(null, e);
			} finally {
				this.conn = null;
			}
		}
		return true;
	}

	private Database openConnection(String url, String user, String pwd)
			throws ClassNotFoundException, SQLException {
		String driver = "com.mysql.jdbc.Driver";
		Class.forName(driver);
		conn = DriverManager.getConnection(url, user, pwd);
		return this;
	}

	public boolean existsTable(String tableName) throws SQLException {
		Statement statement = conn.createStatement();
		String sql = String
				.format("select table_name from `INFORMATION_SCHEMA`.`TABLES` where table_name ='%s';",
						tableName);
		ResultSet rs = statement.executeQuery(sql);
		try {
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} finally {
			rs.close();
		}

	}
}
