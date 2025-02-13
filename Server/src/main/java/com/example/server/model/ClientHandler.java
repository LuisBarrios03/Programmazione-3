package com.example.server.model;


import com.example.server.controller.ServerController;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private MailStorage mailStorage;
    ServerController serverController;

    public ClientHandler(Socket socket, MailStorage mailStorage, ServerController serverController) {
        this.clientSocket = socket;
        this.mailStorage = mailStorage;
        this.serverController = serverController;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String message = reader.readLine();
            if (message != null) {
                serverController.appendLog("Richiesta del client presa in carico: " + message);
                JSONObject request = new JSONObject(message);
                JSONObject response = handleRequest(request);
                writer.println((response.toString()));
            }
        } catch (IOException e) {
            System.err.println("Errore nel gestire la richiesta del client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Errore nel chiudere il socket del client: " + e.getMessage());
            }
        }
    }

    public JSONObject handleRequest (JSONObject request) {
        String operation = request.optString("action", "");
        JSONObject response = new JSONObject();

        switch(operation){
            case "SEND_EMAIL":
                response = handleSendEmail(request);
                break;
            case "GET_MAILBOX":
                response = handleGetMailbox(request);
                break;
            case "DELETE_EMAIL":
                response = handleDeleteEmail(request);
                break;
                case "PING":
                response.put("status", "PONG");
                serverController.appendLog("PONG");
                break;
            case "LOGIN":
                response = handleLogin(request);
                break;
            default:
                response.put("status", "ERRORE");
                response.put("message", "Operazione non valida");
                serverController.appendLog("ERRORE: Operazione non valida");
        }
        return response;
    }

    private JSONObject handleSendEmail(JSONObject request) {
        try {
            String sender = request.getString("mittente");
            List<String> recipients = request.getJSONArray("destinatari").toList().stream().map(Object::toString).toList();
            String subject = request.getString("oggetto");
            String body = request.getString("corpo");
            String date = request.getString("data");

            Email email = new Email(sender, recipients, subject, body, date);

            for (String recipient : recipients){
                MailBox mailbox = mailStorage.loadMailBox(recipient);
                if (mailbox == null){
                    serverController.appendLog("Mailbox non trovata per: " + recipient);
                    return new JSONObject().put("status", "ERRORE").put("message", "Mailbox non trovata");
                }
                mailbox.sendEmail(email);
                mailStorage.saveMailBox(mailbox);
            }
            serverController.appendLog("Email inviata da: " + sender + " a: " + recipients);
            return new JSONObject().put("status", "OK").put("message", "Operazione riuscita");
        } catch (Exception e) {
            serverController.appendLog("ERRORE in handleSendEmail");
            return new JSONObject().put("status", "ERRORE").put("message", e.getMessage());
        }
    }

    private JSONObject handleGetMailbox(JSONObject request) {
        try {
            String account = request.getString("mittente");
            String lastEmailId = request.optString("lastEmailId", "");
            MailBox mailbox = mailStorage.loadMailBox(account);

            if (mailbox == null) {
                return new JSONObject().put("status", "ERRORE").put("message", "Mailbox non trovata");
            }

            List<Email> newEmails = mailbox.getEmails().stream()
                    .filter(email -> email.getId().compareTo(lastEmailId) > 0)
                    .toList();

            serverController.appendLog("Richiesta MailBox da: " + account);
            return new JSONObject().put("status", "OK").put("emails", newEmails);
        } catch (Exception e) {
            serverController.appendLog("ERRORE in handleGetMailbox");
            return new JSONObject().put("status", "ERRORE").put("message", "Richiesta non valida");
        }
    }

    private JSONObject handleDeleteEmail(JSONObject request) {
        try {
            String account = request.getString("mittente");
            String emailId = request.getString("id");

            MailBox mailbox = mailStorage.loadMailBox(account);
            if (mailbox == null || !mailbox.removeEmail(emailId)) {
                serverController.appendLog("ERRORE in handleDeleteEmail: email non trovata");
                return new JSONObject().put("status", "ERRORE").put("message", "Email non trovata");
            }

            mailStorage.saveMailBox(mailbox);
            serverController.appendLog("Email eliminata da: " + account);
            return new JSONObject().put("status", "OK").put("message", "Operazione riuscita");
        } catch (Exception e) {
            serverController.appendLog("ERRORE in handleDeleteEmail: richiesta non valida");
            return new JSONObject().put("status", "ERRORE").put("message", "Richiesta non valida");
        }
    }

    private JSONObject handleLogin(JSONObject request) {
        try {
            JSONObject data = request.getJSONObject("data");
            JSONObject mailObject = data.getJSONObject("mail");
            String sender = mailObject.getString("sender");
            if (mailStorage.isRegisteredEmail(sender)) {
                serverController.appendLog("Login effettuato con: " + sender);
                return new JSONObject().put("status", "OK").put("message", "Operazione riuscita");
            } else {
                serverController.appendLog("ERRORE in handleLogin: sender non registrata");
                return new JSONObject().put("status", "ERRORE").put("message", "Email non registrata");
            }
        } catch (Exception e) {
            serverController.appendLog("ERRORE in handleLogin");
            return new JSONObject().put("status", "ERRORE").put("message", e.getMessage());
        }
    }


}