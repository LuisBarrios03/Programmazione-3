package com.example.server.model;

import java.io.*;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Email email;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String message = reader.readLine(); // Legge il messaggio
            //switch(per le azioni)
            if (message != null && message.startsWith("Email:")) {
                handleEmail(message);
            } else {
                System.out.println("Messaggio non valido.");
            }
        } catch (IOException e) {
            System.out.println("Errore nella comunicazione con il client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Errore nella chiusura del socket.");
            }
        }
    }
    private void handleEmail(String message) throws IOException {
        try {
            email = ToEmail(message);
            if (email == null) {
                throw new IOException("Errore nel parsing dell'email.");
            }
            System.out.println("Messaggio email ricevuto: " + email);
            switch (email.getCause()) {
                case "Inoltro" -> inviaMail(email);
                case "Cancella"->cancellaMail(email);
                case "Login" -> loginClient(email);
                case "Invio"->invioEmail(email);
                case "InoltroTutti"-> inoltroTutti(email);
                default -> throw new IOException("Errore nella causa dell'email.");
            }
        } catch (IOException e) {
            throw new IOException("Conversione fallita.");
        }
    }

    private Email ToEmail(String message) {
        if (message == null || !message.startsWith("Email:")) {
            return null;
        }

        String emailAddress = null, subject = null, body = null, cause = null;
        List<String> recipients = new ArrayList<>();

        // Rimozione "Email:" iniziale
        message = message.substring(6).trim();
        String[] parts = message.split(",");
        for (String part : parts) {
            if (part.startsWith("Recipiens[")) {
                String recipientList = part.substring(10, part.length() - 1).trim(); // Rimuove 'Recipiens[' e ']'
                recipients = Arrays.asList(recipientList.split(",")); // Divide i destinatari
            } else if (part.startsWith("Subject:")) {
                subject = part.substring(8).trim();
            } else if (part.startsWith("Body:")) {
                body = part.substring(5).trim();
            } else if (part.startsWith("cause:")) {
                cause = part.substring(6).trim();
            } else {
                emailAddress = part.trim(); // Prima parte è l'email mittente
            }
        }

        if (emailAddress == null || recipients.isEmpty() || subject == null || body == null || cause == null) {
            return null;
        }

        return new Email(emailAddress, recipients, subject, body,java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) ,cause);
    }

    private boolean loginClient(Email email) {

    }
}
