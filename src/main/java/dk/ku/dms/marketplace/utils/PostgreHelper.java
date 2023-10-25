package dk.ku.dms.marketplace.utils;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.*;

public class PostgreHelper {

	static Connection dbConnection;

    static {
        try {
        	dbConnection = PostgreHelper.getConnection();
            PostgreHelper.initLogTable(dbConnection);
            System.out.println("Connection to DB established ...............");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // https://jdbc.postgresql.org/documentation/datasource/#example111-datasource-code-example
    // https://www.digitalocean.com/community/tutorials/connection-pooling-in-java
    public static Connection getConnection() throws SQLException {
    	
    	Properties properties = new Properties();
		properties.setProperty("user", Constants.user);
		properties.setProperty("password", Constants.password);

		DriverManagerConnectionFactory connectionFactory = new DriverManagerConnectionFactory(Constants.connection_string, properties);

		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

		GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<>();
		config.setMaxTotal(25);
		config.setMaxIdle(10);
		config.setMinIdle(5);

		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, config);
		poolableConnectionFactory.setPool(connectionPool);
		DataSource dataSource = new PoolingDataSource<>(connectionPool);
		
		return dataSource.getConnection();
    }

    public static void initLogTable(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS public.log (\"type\" varchar NULL,\"key\" varchar NULL, value varchar NULL)");
        st.close();
    }

    public static void truncateLogTable(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("TRUNCATE public.log");
        st.close();
    }
    
    public static void log(String type, String key, String value)
    {
    	try
    	{
    		value = value.replace('\'', '\"');
    		Statement st = dbConnection.createStatement();
            String sql = String.format("INSERT INTO public.log (\"type\",\"key\",\"value\") VALUES ('%s', '%s', '%s')", type, key, value);
            st.execute(sql);
    	}
    	catch (Exception e)
    	{
    		System.out.println("Exception happens when writing " + type + " log. " + e.getMessage() + e.getStackTrace().toString());
    	}
    }

}
