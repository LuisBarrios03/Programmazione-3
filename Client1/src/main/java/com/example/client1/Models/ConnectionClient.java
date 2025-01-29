package com.example.client1.Models;

import javafx.application.Platform;
import com.example.client1.Controllers.HomeController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionClient implements Runnable {
    private static boolean running = false;
    private static Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private HomeController homeController;

    public ConnectionClient(HomeController homeController) {
        this.homeController = homeController;
    }

    public static boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        String serverAddress = "localhost";
        int porta = 5000;

        try {
            clientSocket = new Socket(serverAddress, porta);
            clientSocket.setKeepAlive(true); // 🔹 Abilita Keep-Alive sul client
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            running = true;
            Platform.runLater(() -> homeController.changeConnectionStatus(true));

            // 🔹 Invia messaggi di keep-alive ogni 5 secondi
            new Thread(() -> {
                while (running) {
                    try {
                        Thread.sleep(5000);
                        out.println("PING");
                        String response = in.readLine();
                        if (!"PONG".equals(response)) {
                            throw new IOException("Keep-alive failed");
                        }
                    } catch (IOException | InterruptedException e) {
                        running = false;
                        Platform.runLater(() -> homeController.changeConnectionStatus(false));
                        break;
                    }
                }
            }).start();

            while (running) {
                String risposta = in.readLine();
                if (risposta == null) break;
                System.out.println("Risposta dal server: " + risposta);
            }
        } catch (IOException e) {
            running = false;
            Platform.runLater(() -> homeController.changeConnectionStatus(false));
        }
    }

    public static void stopConnection() {
        running = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            System.out.println("Connessione chiusa.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
