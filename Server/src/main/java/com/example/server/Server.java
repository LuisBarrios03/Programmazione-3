package com.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static void ConnectionServer() {
        int porta = 5000; // Porta di ascolto
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Server in ascolto sulla porta " + porta);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Attende una connessione
                System.out.println("Client connesso: " + clientSocket.getInetAddress());

                // Creazione flussi di input/output
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Ricezione e risposta al client
                String messaggio = in.readLine();
                System.out.println("Messaggio ricevuto: " + messaggio);
                out.println("Messaggio ricevuto: " + messaggio);

                clientSocket.close(); // Chiude la connessione con il client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
