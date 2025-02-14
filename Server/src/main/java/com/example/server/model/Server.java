package com.example.server.model;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.example.server.controller.ServerController;

public class Server {
    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private MailStorage mailStorage;
    private ServerController serverController;
    private ExecutorService threadPool;

    public Server(int port, File storageDirectory, ServerController controller, int threadPoolSize) {
        this.port = port;
        this.mailStorage = new MailStorage(storageDirectory);
        this.serverController = controller;
        this.threadPool = Executors.newCachedThreadPool();
    }

    // Metodo per avviare il server
    public void start() {
        serverThread = new Thread(() -> {
            try {
                // Crea i file .bin per gli account se non esistono all'avvio
                mailStorage.createMailBoxesIfNotExist();

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

    // Metodo per fermare il server
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                threadPool.shutdown();
                System.out.println("Server fermato.");
            } catch (IOException e) {
                System.err.println("Errore nel fermare il server: " + e.getMessage());
            }
        }
    }

    // Metodo per iniziare a ricevere nuove connessioni
    public void startReceiving() {
        running = true;
        start();
    }

    // Metodo per terminare il server
    public void stopReceiving() {
        stop();
    }

    // Verifica se una mail è registrata
    public boolean isEmailRegistered(String email) {
        return mailStorage.isRegisteredEmail(email);
    }
}
