package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Server {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        try {
            ServerSocket serverSocket = new ServerSocket(9123);
            System.out.println("Сервер запущен");
            while (true){
                Socket socket = serverSocket.accept(); // Ждём подключения клиентов
                User user = new User(socket);
                System.out.println("Клиент подключился");
                users.add(user);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMessage(user, "Ввдите имя: ");
                            String username = user.getIs().readUTF();
                            user.setName(username);
                            sendMessage(user, "Добро пожаловать на сервер");
                            sendOnlineUsers(users);
                            String request;
                            while (true){
                                request = user.getIs().readUTF();// ждём пока поступит сообщение
                                System.out.println("Сообщение от клиента: "+request);
                                for (User user1 : users) {
                                    if (user.equals(user1)) continue;
                                    sendMessage(user1, user.getName()+": "+request);
                                }
                            }
                        }catch (IOException e){
                            users.remove(user);
                            try {
                                sendOnlineUsers(users);
                            }catch (IOException exception){
                                exception.printStackTrace();
                            }

                        }
                    }
                });
                thread.start();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void sendMessage(User user, String message) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        user.getOut().writeUTF(jsonObject.toJSONString());
    }

    static void sendOnlineUsers(ArrayList<User> users) throws IOException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        for (User user : users) {
            jsonArray.add(user.getName());
        }
        jsonObject.put("onlineUsers", jsonArray);
        for (User user : users) {
            user.getOut().writeUTF(jsonObject.toJSONString());
        }
    }
}
