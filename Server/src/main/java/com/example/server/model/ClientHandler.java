package com.example.server.model;

import com.example.server.controller.EmailAdapter;
import com.example.server.controller.ServerController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * La classe ClientHandler gestisce le richieste dei client connessi al server.
 * Implementa l'interfaccia Runnable per consentire l'esecuzione in un thread separato.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final MailStorage mailStorage;
    private final ServerController serverController;
    private final Gson gson =  new GsonBuilder()
            .registerTypeAdapter(Email.class, new EmailAdapter()) // Registrazione dell'adapter
            .create();

    /**
     * Costruttore della classe ClientHandler.
     *
     * @param socket il socket del client connesso
     * @param mailStorage l'istanza di MailStorage per la gestione delle mailbox
     * @param serverController il controller del server per aggiornare l'interfaccia utente
     */
    public ClientHandler(Socket socket, MailStorage mailStorage, ServerController serverController) {
        this.clientSocket = socket;
        this.mailStorage = mailStorage;
        this.serverController = serverController;
    }

    /**
     * Metodo eseguito quando il thread viene avviato.
     * Gestisce la lettura delle richieste dal client e l'invio delle risposte.
     */
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String message = reader.readLine();
            if (message != null && !message.isEmpty()) {
                serverController.appendLog("Richiesta del client: " + message);
                JsonObject request = JsonParser.parseString(message).getAsJsonObject();
                JsonObject response = handleRequest(request);
                writer.println(gson.toJson(response));
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

    /**
     * Gestisce le richieste ricevute dal client.
     *
     * @param request la richiesta JSON ricevuta dal client
     * @return la risposta JSON da inviare al client
     */
    private JsonObject handleRequest(JsonObject request) {
        String action = request.has("action") ? request.get("action").getAsString() : "";
        switch (action) {
            case "SEND_EMAIL":
                return handleSendEmail(request);
            case "GET_MAILBOX":
                return handleGetMailbox(request);
            case "DELETE_EMAIL":
                return handleDeleteEmail(request);
            case "LOGIN":
                return handleLogin(request);
            case "PING":
                JsonObject pingResponse = new JsonObject();
                pingResponse.addProperty("status", "OK");
                return pingResponse;
            default:
                JsonObject defaultResponse = new JsonObject();
                defaultResponse.addProperty("status", "ERRORE");
                defaultResponse.addProperty("message", "Operazione non valida");
                return defaultResponse;
        }
    }

    /**
     * Gestisce la richiesta di invio email.
     *
     * @param request la richiesta JSON contenente i dati dell'email
     * @return la risposta JSON da inviare al client
     */
    private JsonObject handleSendEmail(JsonObject request) {
        try {
            JsonObject mailJson = request.getAsJsonObject("data").getAsJsonObject("mail");
            Email email = gson.fromJson(mailJson, Email.class);
            if (!hasDuplicates(email.getRecipients(),0)) {
                for (String recipient : email.getRecipients()) {
                    if (isValid(recipient)) {
                        File mailboxFile = new File("data", recipient + ".bin");
                        MailBox mailbox = mailboxFile.exists() ? mailStorage.loadMailBox(recipient) : new MailBox(recipient);
                        mailbox.sendEmail(email);
                        mailStorage.saveMailBox(mailbox);
                    } else {
                        serverController.appendLog("Errore in handleSendEmail: formato non valido");
                        JsonObject response = new JsonObject();
                        response.addProperty("status", "ERRORE");
                        response.addProperty("message", "Formato non valido");
                        return response;
                    }
                }
            } else {
                serverController.appendLog("Errore in handleSendEmail: presenti duplicati");
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "presenti duplicati");
                return response;
            }
            serverController.appendLog("Email inviata da: " + email.getSender());
            JsonObject response = new JsonObject();
            response.addProperty("status", "OK");
            return response;
        } catch (Exception e) {
            serverController.appendLog("Errore in handleSendEmail: " + e.getMessage());
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERRORE");
            response.addProperty("message", e.getMessage());
            return response;
        }
    }

    /**
     * Gestisce la richiesta di recupero della mailbox.
     *
     * @param request la richiesta JSON contenente i dati della mailbox
     * @return la risposta JSON da inviare al client
     */
    private JsonObject handleGetMailbox(JsonObject request) {
        try {
            if (!request.has("data") || !request.get("data").isJsonObject()) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Richiesta malformata");
                return response;
            }

            JsonObject dataObj = request.getAsJsonObject("data");
            JsonObject mailJson;
            if (dataObj.has("mail") && dataObj.get("mail").isJsonObject()) {
                mailJson = dataObj.getAsJsonObject("mail");
            } else {
                mailJson = dataObj;
            }

            if (!mailJson.has("sender")) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Sender non specificato");
                return response;
            }

            String sender = mailJson.get("sender").getAsString();

            if (!mailStorage.isRegisteredEmail(sender)) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Mailbox non trovata");
                return response;
            }

            MailBox mailbox = mailStorage.loadMailBox(sender);
            if (mailbox == null) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Errore nel recupero della mailbox");
                return response;
            }

            JsonObject response = new JsonObject();
            response.addProperty("status", "OK");

            List<Email> emails = mailbox.getEmails();

            if(mailJson.has("lastChecked")){
                String lastChecked = mailJson.get("lastChecked").getAsString();
                LocalDateTime lastDate = LocalDateTime.parse(lastChecked);

                emails = emails.stream()
                        .filter(email -> email.getDate().isAfter(lastDate))
                        .toList();
            }

            response.add("emails", gson.toJsonTree(emails));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Errore durante il recupero della mailbox");
            return response;
        }
    }

    /**
     * Analizza il timestamp dell'ultima verifica.
     *
     * @param lastCheckedStr la stringa del timestamp
     * @return l'istanza di Instant corrispondente
     */
    private Instant parseLastCheckedTimestamp(String lastCheckedStr) {
        try {
            lastCheckedStr = lastCheckedStr.substring(0, Math.min(lastCheckedStr.length(), 23)) + "Z"; // Tronca ai millisecondi
            return Instant.parse(lastCheckedStr);
        } catch (Exception e) {
            return Instant.MIN;
        }
    }

    /**
     * Gestisce la richiesta di eliminazione di un'email.
     *
     * @param request la richiesta JSON contenente i dati dell'email da eliminare
     * @return la risposta JSON da inviare al client
     */
    private JsonObject handleDeleteEmail(JsonObject request) {
        try {
            // La struttura attesa: { "action": "DELETE_EMAIL", "data": { "mail": { "sender": "...", "id": "..." } } }
            JsonObject mailJson = request.getAsJsonObject("data");
            String account = mailJson.get("sender").getAsString();
            String emailId = mailJson.get("id").getAsString();
            File mailboxFile = new File("data", account + ".bin");
            if (!mailboxFile.exists()) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Mailbox non trovata");
                return response;
            }
            MailBox mailbox = MailBox.deserialize(mailboxFile);
            if (mailbox.removeEmail(emailId)) {
                mailbox.serialize(mailboxFile);
                JsonObject response = new JsonObject();
                response.addProperty("status", "OK");
                response.addProperty("message", "Email eliminata");
                return response;
            } else {
                JsonObject response = new JsonObject();
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Email non trovata");
                return response;
            }
        } catch (Exception e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Errore durante l'eliminazione dell'email");
            return response;
        }
    }

    /**
     * Gestisce la richiesta di login.
     *
     * @param request la richiesta JSON contenente i dati di login
     * @return la risposta JSON da inviare al client
     */
    private JsonObject handleLogin(JsonObject request) {
        try {
            // Struttura attesa: { "data": { "mail": { "sender": "..." } } }
            JsonObject mailJson = request.getAsJsonObject("data").getAsJsonObject("mail");
            String sender = mailJson.get("sender").getAsString();
            JsonObject response = new JsonObject();
            if (mailStorage.isRegisteredEmail(sender)) {
                response.addProperty("status", "OK");
                response.addProperty("message", "Login riuscito");
            } else {
                response.addProperty("status", "ERRORE");
                response.addProperty("message", "Email non registrata");
            }
            return response;
        } catch (Exception e) {
            JsonObject response = new JsonObject();
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Errore nel login");
            return response;
        }
    }

    /**
     * Verifica se un account email è valido.
     *
     * @param account l'account email da verificare
     * @return true se l'account è valido, false altrimenti
     */
    private boolean isValid(String account) {
        return account != null && account.matches("^[a-zA-Z0-9._%+-]+@notamail\\.com$");
    }

    /**
     * Verifica se una lista di destinatari contiene duplicati.
     *
     * @param recipients la lista dei destinatari
     * @param index l'indice corrente per la verifica
     * @return true se ci sono duplicati, false altrimenti
     */
    private static boolean hasDuplicates(List<String> recipients, int index) {
        // Caso base: se abbiamo controllato tutti gli elementi, non ci sono duplicati
        if (index >= recipients.size() - 1) {
            return false;
        }
        String recipient = recipients.get(index);

        // Controlla se il recipient corrente appare nelle posizioni successive
        for (int i = index+1; i < recipients.size(); i++) {
            if (recipient.equals(recipients.get(i))) {
                return true;
            }
        }

        // Passa al prossimo elemento ricorsivamente
        return hasDuplicates(recipients, index + 1);
    }

}