package com.example.server.model;

import com.example.server.controller.ServerController;
import org.json.JSONObject;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final MailStorage mailStorage;
    private final ServerController serverController;

    public ClientHandler(Socket socket, MailStorage mailStorage, ServerController serverController) {
        this.clientSocket = socket;
        this.mailStorage = mailStorage;
        this.serverController = serverController;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String message = reader.readLine();
            if (message != null) {
                serverController.appendLog("Richiesta del client: " + message);
                JSONObject request = new JSONObject(message);
                JSONObject response = handleRequest(request);
                writer.println(response.toString());
            }
        } catch (IOException e) {
            System.err.println("Errore nella gestione del client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Errore nella chiusura del socket: " + e.getMessage());
            }
        }
    }

    private JSONObject handleRequest(JSONObject request) {
        return switch (request.optString("action", "")) {
            case "SEND_EMAIL" -> handleSendEmail(request);
            case "GET_MAILBOX" -> handleGetMailbox(request);
            case "DELETE_EMAIL" -> handleDeleteEmail(request);
            case "LOGIN" -> handleLogin(request);
            case "PING" -> new JSONObject().put("status", "OK");
            default -> new JSONObject().put("status", "ERRORE").put("message", "Operazione non valida");
        };
    }

    private JSONObject handleSendEmail(JSONObject request) {
        try {
            Email email = Email.fromJson(request.getJSONObject("email"));
            for (String recipient : email.getRecipients()) {
                File mailboxFile = new File(recipient + ".json");
                MailBox mailbox = mailboxFile.exists() ? MailBox.deserializeFromJson(mailboxFile) : new MailBox(recipient);
                mailbox.sendEmail(email);
                mailbox.serializeToJson(mailboxFile);
            }
            serverController.appendLog("Email inviata da: " + email.getSender());
            return new JSONObject().put("status", "OK");
        } catch (Exception e) {
            serverController.appendLog("Errore in handleSendEmail: " + e.getMessage());
            return new JSONObject().put("status", "ERRORE").put("message", e.getMessage());
        }
    }

    private JSONObject handleGetMailbox(JSONObject request) {
        try {
            String account = request.getString("mittente");
            File mailboxFile = new File(account + ".json");
            if (!mailboxFile.exists()) {
                return new JSONObject().put("status", "ERRORE").put("message", "Mailbox non trovata");
            }
            MailBox mailbox = MailBox.deserializeFromJson(mailboxFile);
            return new JSONObject().put("status", "OK").put("emails", mailbox.getEmails().stream().map(Email::toJson).toList());
        } catch (Exception e) {
            return new JSONObject().put("status", "ERRORE").put("message", "Errore durante il recupero della mailbox");
        }
    }

    private JSONObject handleDeleteEmail(JSONObject request) {
        try {
            String account = request.getString("mittente");
            String emailId = request.getString("id");
            File mailboxFile = new File(account + ".json");
            if (!mailboxFile.exists()) {
                return new JSONObject().put("status", "ERRORE").put("message", "Mailbox non trovata");
            }
            MailBox mailbox = MailBox.deserializeFromJson(mailboxFile);
            if (mailbox.removeEmail(emailId)) {
                mailbox.serializeToJson(mailboxFile);
                return new JSONObject().put("status", "OK").put("message", "Email eliminata");
            } else {
                return new JSONObject().put("status", "ERRORE").put("message", "Email non trovata");
            }
        } catch (Exception e) {
            return new JSONObject().put("status", "ERRORE").put("message", "Errore durante l'eliminazione dell'email");
        }
    }

    private JSONObject handleLogin(JSONObject request) {
        try {
            String sender = request.getJSONObject("data").getJSONObject("mail").getString("sender");
            if (mailStorage.isRegisteredEmail(sender)) {
                return new JSONObject().put("status", "OK").put("message", "Login riuscito");
            } else {
                return new JSONObject().put("status", "ERRORE").put("message", "Email non registrata");
            }
        } catch (Exception e) {
            return new JSONObject().put("status", "ERRORE").put("message", "Errore nel login");
        }
    }
}
