package com.example.server.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.server.model.Email;
import com.example.server.model.MailBox;

public class Server {
    private static boolean running = false;
    private static ServerSocket serverSocket;
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final Map<String, MailBox> mailboxes = new HashMap<>();

    public synchronized void sendEmail(Email email) {
        for (String recipient : email.getRecipients()) {
            MailBox mailbox = mailboxes.get(recipient);
            if (mailbox != null) {
                mailbox.addEmail(email);
                System.out.println("Email delivered to: " + recipient);
            } else {
                System.out.println("Error: Recipient not found - " + recipient);
            }
        }
    }

    public static void startServer(int porta) {
        if (running) return;
        running = true;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(porta);
                System.out.println("Server avviato sulla porta " + porta);

                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setKeepAlive(true);
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
                client.stopClient();
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

