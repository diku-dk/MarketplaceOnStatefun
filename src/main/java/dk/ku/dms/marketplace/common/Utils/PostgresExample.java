package dk.ku.dms.marketplace.common.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PostgresExample {
    public static void main(String[] args) {
        // PostgreSQL连接信息
        String url = "jdbc:postgresql://postgres:5432/ucloud";
        String user = "ucloud";
        String password = "ucloud";

        try {
            // 加载PostgreSQL JDBC驱动程序
            Class.forName("org.postgresql.Driver");

            // 建立连接
            Connection connection = DriverManager.getConnection(url, user, password);

            // 创建Statement对象
            Statement statement = connection.createStatement();

            // 执行SQL查询
            String sql = "SELECT * FROM your_table_name";
            ResultSet resultSet = statement.executeQuery(sql);

            // 处理查询结果
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                System.out.println("ID: " + id + ", Name: " + name);
            }

            // 关闭资源
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
