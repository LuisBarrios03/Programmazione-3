package com.example.client1.Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionClient {
    public void ConnectionServer() {
        String serverAddress = "localhost"; // IP del server
        int porta = 5000; // Porta su cui connettersi

        try (Socket socket = new Socket(serverAddress, porta);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Invio di un messaggio al server
            out.println("Ciao, Server!");

            // Ricezione della risposta
            String risposta = in.readLine();
            System.out.println("Risposta dal server: " + risposta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
