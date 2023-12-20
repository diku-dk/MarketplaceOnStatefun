package dk.ku.dms.marketplace.utils;

import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

public final class PostgresHelper {

	private static Connection dbConnection;

    private static DataSource dataSource;

    static {

        if(Constants.logging) {
            try {
                Properties properties = new Properties();
                properties.setProperty("user", Constants.user);
                properties.setProperty("password", Constants.password);

                DriverManagerConnectionFactory connectionFactory = new DriverManagerConnectionFactory(Constants.connectionString, properties);

                PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

                GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<>();
                config.setMaxTotal(Constants.maxPoolSize);
                config.setMaxIdle(10);
                config.setMinIdle(5);

                ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, config);
                poolableConnectionFactory.setPool(connectionPool);
                dataSource = new PoolingDataSource<>(connectionPool);

                dbConnection = PostgresHelper.getConnection();
                PostgresHelper.initLogTable(dbConnection);
                System.out.println("Connection to DB established ...............");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // https://jdbc.postgresql.org/documentation/datasource/#example111-datasource-code-example
    // https://www.digitalocean.com/community/tutorials/connection-pooling-in-java
    public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
    }

    public static void initLogTable(Connection conn) throws SQLException {
        Statement st = conn.createStatement();
        st.execute("CREATE TABLE IF NOT EXISTS public.log (\"type\" varchar NULL,\"key\" varchar NULL, value varchar NULL)");
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
    		System.out.println("Exception happens when writing " + type + " log. " + e.getMessage() + Arrays.toString(e.getStackTrace()));
    	}
    }

    public static void init(){
        System.out.println("Init postgres helper success");
    }

}
