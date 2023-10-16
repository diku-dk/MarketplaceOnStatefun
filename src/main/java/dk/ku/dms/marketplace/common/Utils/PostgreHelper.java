package dk.ku.dms.marketplace.common.Utils;

import java.sql.*;

public class PostgreHelper {

    // https://jdbc.postgresql.org/documentation/datasource/#example111-datasource-code-example
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://postgres:5432/ucloud";
        return DriverManager.getConnection(url, "ucloud", "ucloud");
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

}
