package com.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean running = true;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            while (running) {
                String messaggioClient = in.readLine();
                if (messaggioClient == null) {
                    System.out.println("Connessione persa con il client.");
                    break; // 🔹 Il client ha chiuso la connessione
                }

                if (messaggioClient.equals("PING")) {
                    out.println("PONG");  // 🔹 Risponde ai messaggi di keep-alive
                } else {
                    System.out.println("Messaggio ricevuto: " + messaggioClient);
                    out.println("Ciao, Client! Messaggio ricevuto.");
                }
            }
        } catch (IOException e) {
            System.out.println("Connessione persa con il client.");
        } finally {
            stopClient();
        }
    }

    public void stopClient() {
        running = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
