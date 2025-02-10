package com.example.server.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Email email;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
         //TODO:
            //accetta il socket in entrata
            //legge il messaggio
            //controlla i valori null
            //chiama il metodo che serve
            //manda conferma di ciò che è stato fatto
            //lock ()
            synchronized (this) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message = reader.readLine(); // Legge il messaggio
                //gestione for
                if (message != null && message.startsWith("Email:")) {
                    handleEmail(message);
                } else {
                    throw new IllegalArgumentException("Messaggio non valido.");
                }
            }
        } catch (IOException e) {
            System.out.println("Comunicazione con il client non riuscita.");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Errore nella chiusura del socket.");
            }
        }
    }
    private void handleEmail(String message) throws IOException {
        // Logica per elaborare il messaggio come email
        try{
            email = ToEmail(message);
            System.out.println("Messaggio email ricevuto: " + message);
            switch(message.cause){
                case "Inoltro": inoltraMail(email); break;
                case "Cancella": cancellaMail(email); break;
                case "Login": loginClient(email); break;
                case "Invio": invioEmail(email); break;
                case "InoltroTutti": inoltroTutti(email); break;
            }
        } catch (IOException e) {
            throw new IOException("Conversione fallita.");
        }
    }

    private Email ToEmail(String converteMe){
        return null;
    }

    private boolean loginClient(Email utente) throws IOException{
        try (ServerSocket serverSocket = new ServerSocket(5001) ){
            File file = new File("data/" + utente.getSender() + ".json");

            Socket socket = serverSocket.accept(); // Accetta una connessione
            System.out.println("Connessione accettata da " + socket.getInetAddress());

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);


            if (file.exists()) {
                writer.println(true);

                System.out.println("Conferma inviata al client.");
                socket.close(); // Chiude la connessione dopo l'invio

                return true;
            } else {
                writer.println(false);

                System.out.println("Rifiuto inviato al client.");
                socket.close(); // Chiude la connessione dopo l'invio

                return false;
            }
        } catch (IOException e){
            System.out.println("Login Fallito");
            return false;
        }
    }
}
