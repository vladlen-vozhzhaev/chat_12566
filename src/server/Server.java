package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(9123);
            System.out.println("Сервер запущен");
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
            while (true){
                Socket socket = serverSocket.accept(); // Ждём подключения клиентов
                User user = new User(socket);
                System.out.println("Клиент подключился");
                users.add(user);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMessage(user, "Для регистрации /reg, \n для авторизации /login");
                            String command = Message.readMessage(user).getMsg();

                            while (true){
                                if(command.equals("/reg")){// Регистрируем
                                    user.reg();
                                    break;
                                }else if (command.equals("/login")){ // Авторизуем
                                    if(user.login()) break;
                                }else{
                                    sendMessage(user, "Неверная команда");
                                }
                            }

                            sendMessage(user, "Добро пожаловать на сервер");
                            sendOnlineUsers(users);
                            Message.sendHistoryMessage(user, 0);
                            while (true){
                                Message message = Message.readMessage(user);
                                if(message == null) continue;
                                message.save();
                                System.out.println("Сообщение от клиента: "+message.getMsg());
                                for (User user1 : users) {
                                    if (user.equals(user1)) continue;
                                    else if (user1.getUserId() == message.getToId()) {
                                        sendMessage(user1, user.getName()+": "+message.getMsg());
                                    } else if (message.getToId() == 0) {
                                        sendMessage(user1, user.getName()+": "+message.getMsg());
                                    }
                                }
                            }
                        }catch (IOException e){
                            users.remove(user);
                            System.out.println("Клиент "+user.getName()+" отключился");
                            try {
                                sendOnlineUsers(users);
                            }catch (IOException exception){
                                exception.printStackTrace();
                            }

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thread.start();

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void sendMessage(User user, String message) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        user.getOut().writeUTF(jsonObject.toJSONString());
    }

    static void sendOnlineUsers(ArrayList<User> users) throws IOException {
        JSONArray jsonArray = new JSONArray(); // jsonArray = []
        JSONObject jsonObject = new JSONObject(); // jsonObject = {}
        for (User user : users) {
            JSONObject jsonUser = new JSONObject(); // jsonUser = {}
            jsonUser.put("id", user.getUserId());
            jsonUser.put("name", user.getName());
            // jsonUser = {"id": 1, "name": "Ivan"}
            jsonArray.add(jsonUser);
        }
        // jsonArray = [{"id": 1, "name": "Ivan"},{"id": 2, "name": "Oleg"},{"id": 3, "name": "Igor"}]
        jsonObject.put("onlineUsers", jsonArray); // jsonObject = {"onlineUsers": [{"id": 1, "name": "Ivan"},{"id": 2, "name": "Oleg"},{"id": 3, "name": "Igor"}]}
        for (User user : users) {
            user.getOut().writeUTF(jsonObject.toJSONString());
        }
    }
}
