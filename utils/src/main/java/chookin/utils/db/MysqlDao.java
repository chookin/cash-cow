package chookin.utils.db;

import java.sql.Connection;
import java.sql.Statement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL的JDBC URL格式：
    jdbc:mysql//[hostname][:port]/[dbname][?param1=value1][&param2=value2]….
    for example：jdbc:mysql://localhost:3306/sample_db?user=root&password=your_password

 * Created by chookin on 7/19/14.
 */
public class MysqlDao {
    String connectionString = null;
    Connection conn = null;
    static {
        try {
            // registered jdbc driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
    public MysqlDao setConnectionString(String str){
        this.connectionString = str;
        return this;
    }

    /**
     * get db connection
     * @return db connection
     */
    public Connection getConn() throws SQLException {
        if (this.conn != null)
            return this.conn;
        try {
            this.conn = DriverManager.getConnection(this.connectionString);
        } finally {
            this.conn = null;
        }
        return this.conn;
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Statement stat = this.getConn().createStatement();
        ResultSet rs = null;
        try{
            rs = stat.executeQuery(sql);
        }finally {
            stat.close();
        }
        return rs;
    }

    public int executeUpdate(String sql) throws SQLException{
        Statement stat = this.getConn().createStatement();
        try {
            int count = stat.executeUpdate(sql);
            return count;
        }finally {
            stat.close();
        }
    }
}
