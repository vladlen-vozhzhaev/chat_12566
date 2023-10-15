package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 9123);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream is = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String response = is.readUTF();
                            System.out.println(response);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
            while (true){
                String message = scanner.nextLine();
                out.writeUTF(message);
            }
        } catch (IOException e) {
            System.out.println("Сервер недоступен!");
        }
    }
}