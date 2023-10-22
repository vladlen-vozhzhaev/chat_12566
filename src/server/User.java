package server;

import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

public class User {
    private String name;
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream out;
    private int userId;
    public User(Socket socket) throws IOException {
        this.socket = socket;
        this.is = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataInputStream getIs() {
        return is;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public boolean reg() throws IOException, SQLException, ParseException {
        Connection connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
        Statement statement = connection.createStatement();
        Server.sendMessage(this, "Введите Имя");
        String name = Message.readMessage(this).getMsg();
        Server.sendMessage(this, "Введите логин");
        String login = Message.readMessage(this).getMsg();
        Server.sendMessage(this, "Введите пароль");
        String pass = Message.readMessage(this).getMsg();
        // ДЗ: проверить, можно ли зарегистрировать пользователя (проверить что такого логина нет)
        statement.executeUpdate("INSERT INTO `users`(`name`, `login`, `pass`) VALUES ('"+name+"','"+login+"','"+pass+"')");
        statement.close();
        return true;
    }
    public boolean login() throws IOException, SQLException, ParseException {
        Connection connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
        Statement statement = connection.createStatement();
        Server.sendMessage(this, "Введите логин");
        String login = Message.readMessage(this).getMsg();
        Server.sendMessage(this, "Введите пароль");
        String pass = Message.readMessage(this).getMsg();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `login`='"+login+"' AND `pass`='"+pass+"'");
        if(resultSet.next()){
            String name = resultSet.getString("name");
            int userId = resultSet.getInt("id");
            this.setName(name);
            this.setUserId(userId);
            return true;
        }else{
            Server.sendMessage(this, "Неправильный логин или пароль");
            return false;
        }
    }
}
