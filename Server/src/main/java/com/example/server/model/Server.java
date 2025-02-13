package com.example.server.model;

import com.example.server.controller.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private int port;
    private boolean running;
    private final MailStorage mailStorage;
    private final ExecutorService threadPool;
    private Thread serverThread;
    private ServerSocket serverSocket;
    ServerController serverController;

    public Server(int port, int maxClients, MailStorage mailStorage, ServerController serverController) {
        this.port = port;
        this.mailStorage = mailStorage;
        this.running = true;
        this.threadPool = Executors.newFixedThreadPool(maxClients);
        this.serverController = serverController;
    }

    public void start() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server Online sulla porta: " + port);
                serverController.appendLog("Server Online sulla porta: " + port);
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                        threadPool.submit(new ClientHandler(clientSocket, mailStorage, serverController));
                    } catch (IOException e) {
                        if (!running) break;
                        System.err.println("Errore nell'accettare la connessione del client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Errore nell'avvio del server: " + e.getMessage());
            }
        });
        serverThread.start();
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Errore nella chiusura del ServerSocket: " + e.getMessage());
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
        threadPool.shutdownNow();
        try {
            if (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                System.err.println("Il thread pool non si è fermato");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Server fermato.");
    }
}

