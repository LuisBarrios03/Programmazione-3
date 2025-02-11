package com.example.server.model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private  boolean running = false;
    private  ServerSocket serverSocket;

    /*Protocollo*/
    public  void startServer(int porta) {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(porta);
                System.out.println("Server avviato sulla porta " + porta);

                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connesso: " + clientSocket.getInetAddress());
                    new Thread(new ClientHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                } else {
                    System.out.println("Server chiuso correttamente.");
                }
            }
        }).start();
    }

    public  void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server spento.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

