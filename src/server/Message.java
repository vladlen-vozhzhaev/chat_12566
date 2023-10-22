package server;

import java.sql.*;

public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String msg;
    private Date date;

    public Message(int fromId, int toId, String msg) {
        this.fromId = fromId;
        this.toId = toId;
        this.msg = msg;
    }
    public void save(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO messages (from_id, to_id, msg) VALUES ('"+fromId+"','"+toId+"','"+msg+"')");
        } catch (SQLException e) {
            System.out.println("Не удалось сохранить сообщение в базу данных");
        }

    }
}
