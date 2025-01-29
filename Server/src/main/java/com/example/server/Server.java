package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static boolean running = false;
    private static ServerSocket serverSocket;
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void startServer(int porta) {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(porta);
                System.out.println("Server avviato sulla porta " + porta);

                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setKeepAlive(true);  // 🔹 Abilita il Keep-Alive sul socket
                    System.out.println("Client connesso: " + clientSocket.getInetAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
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

    public static void stopServer() {
        running = false;
        try {
            for (ClientHandler client : clients) {
                client.stopClient();  // 🔹 Chiude tutti i client connessi
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server spento.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRunning() {
        return running;
    }
}

