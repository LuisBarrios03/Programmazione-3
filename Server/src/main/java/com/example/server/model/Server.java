package com.example.server.model;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.example.server.controller.ServerController;

/**
 * @brief Classe Server che gestisce connessioni e client
 */
public class Server {
    private int port; ///< Porta del server
    private boolean running = false; ///< Stato del server
    private ServerSocket serverSocket; ///< Socket del server
    private Thread serverThread; ///< Thread principale del server
    private MailStorage mailStorage; ///< Gestore della memorizzazione delle mail
    private ServerController serverController; ///< Controller dell'interfaccia
    private ExecutorService threadPool; ///< Thread pool per la gestione dei client

    /**
     * @brief Costruttore del server
     * @param port Porta per le connessioni
     * @param storageDirectory Cartella di memorizzazione delle mail
     * @param controller Controller per l'interfaccia
     * @param threadPoolSize Dimensione del pool di thread
     */
    public Server(int port, File storageDirectory, ServerController controller, int threadPoolSize) {
        this.port = port;
        this.mailStorage = new MailStorage(storageDirectory);
        this.serverController = controller;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * @brief Avvia il server e accetta connessioni
     */
    public void start() {
        serverThread = new Thread(() -> {
            try {
                mailStorage.createMailBoxesIfNotExist(); ///< Inizializza le caselle di posta

                serverSocket = new ServerSocket(port);
                System.out.println("Server Online sulla porta: " + port);
                serverController.appendLog("Server Online sulla porta: " + port);
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept(); ///< Accetta connessioni dai client
                        System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                        threadPool.submit(new ClientHandler(clientSocket, mailStorage, serverController)); ///< Avvia un handler per ogni client
                    } catch (IOException e) {
                        if (!running) break;
                        System.err.println("Errore nell'accettare la connessione del client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Errore nell'avvio del server: " + e.getMessage());
            }
        });
        serverThread.start(); ///< Avvia il thread del server
    }

    /**
     * @brief Ferma il server e chiude tutte le connessioni
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close(); ///< Chiude il socket del server
                threadPool.shutdown(); ///< Termina il thread pool
                System.out.println("Server fermato.");
            } catch (IOException e) {
                System.err.println("Errore nel fermare il server: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Avvia il server per ricevere connessioni
     */
    public void startReceiving() {
        running = true;
        start();
    }

    /**
     * @brief Ferma il server e interrompe la ricezione
     */
    public void stopReceiving() {
        stop();
    }
}
