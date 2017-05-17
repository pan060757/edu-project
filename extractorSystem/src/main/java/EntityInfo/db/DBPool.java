package EntityInfo.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by binbin on 15/9/18.
 */
public class DBPool {

    private static DBPool dbPool;
    private ComboPooledDataSource dataSource;

    static {

        dbPool = new DBPool();
    }

    public DBPool() {
        try {
            ResourceBundle rb= ResourceBundle.getBundle("db");
            dataSource = new ComboPooledDataSource();
            dataSource.setUser(rb.getString("username"));
            dataSource.setPassword(rb.getString("password"));
            dataSource.setJdbcUrl(rb.getString("url"));
            dataSource.setDriverClass(rb.getString("driver"));
            dataSource.setInitialPoolSize(Integer.valueOf(rb.getString("initialpool")));
            dataSource.setAcquireIncrement(Integer.valueOf(rb.getString("acquireIncrement")));
            dataSource.setMinPoolSize(Integer.valueOf(rb.getString("minPoolSize")));
            dataSource.setMaxPoolSize(Integer.valueOf(rb.getString("maxPoolSize")));
            dataSource.setMaxStatements(Integer.valueOf(rb.getString("maxStatements")));
            dataSource.setMaxIdleTime(Integer.valueOf(rb.getString("maxIdleTime")));
            dataSource.setAutomaticTestTable(rb.getString("automaticTestTable"));
            dataSource.setTestConnectionOnCheckin(Boolean.parseBoolean(rb.getString("testConnectionOnCheckin")));
            dataSource.setIdleConnectionTestPeriod(Integer.parseInt(rb.getString("idleConnectionTestPeriod")));
            dataSource.setTestConnectionOnCheckout(Boolean.parseBoolean(rb.getString("testConnectionOnCheckout")));
            dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(rb.getString("breakAfterAcquireFailure")));
            dataSource.setNumHelperThreads(Integer.parseInt(rb.getString("numHelperThreads")));
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    public final static DBPool getInstance() {
        return dbPool;
    }

    public final Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("无法从数据源获取连接 ", e);
        }
    }

    public static void releaseConnection(ResultSet results, PreparedStatement pstmt, Connection connection){

        try {
            if (null != results) {
                results.close();
                results = null;
            }
            if (null != pstmt) {
                pstmt.close();
                pstmt = null;
            }
            if (null != connection) {
                connection.close();
                connection = null;
            }
        } catch (Exception e2) {
            // TODO: handle exception
        }

    }

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        try {
            con = DBPool.getInstance().getConnection();
        } catch (Exception e) {
        } finally {
            if (con != null)
                con.close();
        }
    }
}

