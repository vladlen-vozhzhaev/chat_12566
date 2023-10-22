package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
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

    public String getMsg() {
        return msg;
    }

    public int getToId() {
        return toId;
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

    public static Message readMessage(User user) throws IOException, ParseException, SQLException {
        JSONParser jsonParser = new JSONParser();
        String request = user.getIs().readUTF();// ждём пока поступит сообщение
        JSONObject jsonObject = (JSONObject) jsonParser.parse(request);
        if(jsonObject.containsKey("targetHistory")){
            int toId = Integer.parseInt(jsonObject.get("targetHistory").toString());
            sendHistoryMessage(user, toId);
            return null;
        }else if (jsonObject.containsKey("message")){
            int toId = Integer.parseInt(jsonObject.get("toId").toString());
            String msg = jsonObject.get("message").toString();
            return new Message(user.getUserId(), toId, msg);
        }
        return null;
    }

    public static void sendHistoryMessage(User user, int targetId) throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(Database.DB_URL, Database.DB_LOGIN, Database.DB_PASSWORD);
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        if(targetId == 0){
            resultSet = statement.executeQuery("SELECT * FROM messages WHERE to_id = 0");
        }else{
            resultSet = statement.executeQuery("SELECT * FROM `messages` WHERE to_id in("+user.getUserId()+","+targetId+") AND from_id in("+user.getUserId()+","+targetId+")");
        }
        while (resultSet.next()){
            Server.sendMessage(user, resultSet.getString("msg"));
        }
    }
}
