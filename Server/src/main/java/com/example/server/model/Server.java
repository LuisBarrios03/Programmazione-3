package com.example.server.model;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.example.server.controller.ServerController;

/**
 * La classe Server gestisce un server che accetta connessioni client e gestisce le email.
 */
public class Server {
    private int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private Thread serverThread;
    private MailStorage mailStorage;
    private ServerController serverController;
    private ExecutorService threadPool;

    /**
     * Costruttore della classe Server.
     *
     * @param port la porta su cui il server ascolterà le connessioni
     * @param storageDirectory la directory in cui verranno salvate le mailbox
     * @param controller il controller del server
     * @param threadPoolSize la dimensione del pool di thread
     */
    public Server(int port, File storageDirectory, ServerController controller, int threadPoolSize) {
        this.port = port;
        this.mailStorage = new MailStorage(storageDirectory);
        this.serverController = controller;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * Avvia il server e inizia ad accettare connessioni client.
     */
    public void start() {
        serverThread = new Thread(() -> {
            try {
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

    /**
     * Ferma il server e chiude tutte le connessioni.
     */
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

    /**
     * Inizia a ricevere connessioni client.
     */
    public void startReceiving() {
        running = true;
        start();
    }

    /**
     * Smette di ricevere connessioni client.
     */
    public void stopReceiving() {
        stop();
    }
}