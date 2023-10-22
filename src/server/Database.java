package server;

import java.sql.*;

public class Database {
    static final String DB_URL = "jdbc:mysql://127.0.0.1/chat_12566";
    static final String DB_LOGIN = "root";
    static final String DB_PASSWORD = "";
    static Connection connection;
    static Statement statement;

    public static ResultSet query(String sql){
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static void update(String sql){
        try {
            connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
