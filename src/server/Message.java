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
        Database.update("INSERT INTO messages (from_id, to_id, msg) VALUES ('"+fromId+"','"+toId+"','"+msg+"')");
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
    static void sendMessage(User user, String message) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        user.getOut().writeUTF(jsonObject.toJSONString());
    }

    public static void sendHistoryMessage(User user, int targetId) throws SQLException, IOException {
        ResultSet resultSet;
        if(targetId == 0){
            resultSet = Database.query("SELECT users.name, messages.msg, messages.to_id, messages.from_id, messages.created_at FROM messages, users WHERE to_id = 0 and from_id=users.id;");
        }else{
            resultSet = Database.query("SELECT users.name, messages.msg, messages.to_id, messages.from_id, messages.created_at FROM `messages`, users WHERE to_id in("+user.getUserId()+","+targetId+") AND from_id in("+user.getUserId()+","+targetId+") AND (from_id=users.id)");
        }
        while (resultSet.next()){
            sendMessage(user, resultSet.getString("name")+": "+resultSet.getString("msg"));
        }
    }
}
