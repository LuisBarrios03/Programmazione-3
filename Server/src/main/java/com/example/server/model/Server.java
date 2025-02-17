package com.example.server.model;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.example.server.controller.ServerController;

/**
 * @brief La classe Server gestisce un server che accetta connessioni client e gestisce le email.
 */
public class Server {
    /** @brief Porta su cui il server è in ascolto */
    private int port;
    /** @brief Stato del server: true se attivo, false se fermo */
    private boolean running = false;
    /** @brief Socket principale per l'ascolto delle connessioni */
    private ServerSocket serverSocket;
    /** @brief Thread principale per il ciclo di ascolto */
    private Thread serverThread;
    /** @brief Archivio per la gestione delle email */
    private MailStorage mailStorage;
    /** @brief Controller per l'interfaccia utente del server */
    private ServerController serverController;
    /** @brief Pool di thread per gestire i client in parallelo */
    private ExecutorService threadPool;

    /**
     * @brief Costruttore della classe Server.
     * @param port la porta su cui il server ascolterà le connessioni
     * @param storageDirectory la directory in cui verranno salvate le mailbox
     * @param controller il controller del server
     * @param threadPoolSize la dimensione del pool di thread
     */
    public Server(int port, File storageDirectory, ServerController controller, int threadPoolSize) {
        this.port = port;
        this.mailStorage = new MailStorage(storageDirectory);
        this.serverController = controller;
        this.threadPool = Executors.newCachedThreadPool(); /// Inizializza il pool di thread
    }

    /**
     * @brief Avvia il server e inizia ad accettare connessioni client.
     */
    public void start() {
        serverThread = new Thread(() -> {
            try {
                mailStorage.createMailBoxesIfNotExist(); /// Crea le caselle email se non esistono

                serverSocket = new ServerSocket(port); /// Inizializza il server socket
                System.out.println("Server Online sulla porta: " + port);
                serverController.appendLog("Server Online sulla porta: " + port);

                /// Ciclo principale per accettare le connessioni
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept(); /// Attende un client
                        System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                        threadPool.submit(new ClientHandler(clientSocket, mailStorage, serverController)); /// Gestisce il client
                    } catch (IOException e) {
                        if (!running) break;
                        System.err.println("Errore nell'accettare la connessione del client: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Errore nell'avvio del server: " + e.getMessage());
            }
        });
        serverThread.start(); /// Avvia il thread principale
    }

    /**
     * @brief Ferma il server e chiude tutte le connessioni.
     */
    public void stop() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close(); /// Chiude il server socket
                threadPool.shutdown(); /// Ferma il pool di thread
                System.out.println("Server fermato.");
            } catch (IOException e) {
                System.err.println("Errore nel fermare il server: " + e.getMessage());
            }
        }
    }

    /**
     * @brief Inizia a ricevere connessioni client.
     */
    public void startReceiving() {
        running = true; /// Imposta lo stato su attivo
        start();
    }

    /**
     * @brief Smette di ricevere connessioni client.
     */
    public void stopReceiving() {
        stop();
    }
}
