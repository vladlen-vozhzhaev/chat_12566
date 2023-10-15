package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
                            user.getOut().writeUTF("Ввдите имя: ");
                            String username = user.getIs().readUTF();
                            user.setName(username);
                            user.getOut().writeUTF("Добро пожаловать на сервер");
                            String request;
                            while (true){
                                request = user.getIs().readUTF();// ждём пока поступит сообщение
                                System.out.println("Сообщение от клиента: "+request);
                                for (User user1 : users) {
                                    if (user.equals(user1)) continue;
                                    user1.getOut().writeUTF(user.getName()+": "+request);
                                }
                            }
                        }catch (IOException e){
                            users.remove(user);
                        }
                    }
                });
                thread.start();

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
