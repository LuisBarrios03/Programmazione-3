package com.example.client1.Controllers;

import com.example.client1.Models.Email;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
    private final int serverPort;
    private final String serverAddress;

    public ServerHandler(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    public JSONObject sendCommand(JSONObject data) throws IOException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(data);
            String response = in.readLine();
            return (response != null) ? receiveMessage(response)
                    : new JSONObject().put("status", "ERRORE").put("message", "Nessuna risposta dal server");
        }
    }

    public JSONObject receiveMessage(String responseString) {
        JSONObject response = new JSONObject(responseString);
        if (response.optString("status").equals("OK")) {
            System.out.println("Operazione riuscita: " + response.optString("message"));
        } else {
            System.err.println("Errore: " + response.optString("message"));
        }
        return response;
    }

    public void serializeToJson(String filePath, JSONObject data) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data.toString(4));
        }
    }

    public JSONObject deserializeFromJson(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONObject(content);
    }

    public List<Email> createInbox(JSONObject data) {
        JSONArray mailList = data.optJSONArray("MailList");
        List<Email> mailListConverted = new ArrayList<>();
        for (int i = 0; i < mailList.length(); i++) {
            JSONObject mail = mailList.getJSONObject(i);
            List<String> recipients = mail.optJSONArray("recipients").toList().stream().map(Object::toString).toList();
            Email email = new Email(
                    mail.getString("id"),
                    mail.getString("sender"),
                    recipients,
                    mail.getString("subject"),
                    mail.getString("body"),
                    mail.optString("timestamp", LocalDateTime.now().toString())
            );
            mailListConverted.add(email);
        }
        return mailListConverted;
    }

    public String getLastId(List<Email> emails) {
        return emails.stream()
                .map(Email::getId)
                .filter(id -> id.matches("\\d+")) // Filtra solo ID numerici
                .mapToInt(Integer::parseInt)
                .max()
                .stream()
                .mapToObj(String::valueOf)
                .findFirst()
                .orElse("0");
    }

    /**
     * Metodo per verificare la connessione al server.
     *
     * @return JSONObject contenente lo stato della connessione e un messaggio.
     */
    public JSONObject tryConnection() {
        JSONObject response = new JSONObject();
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            // Connessione riuscita
            response.put("status", "OK");
            response.put("message", "Connesso al server con successo.");
        } catch (IOException e) {
            // Errore durante la connessione
            response.put("status", "ERRORE");
            response.put("message", "Impossibile connettersi al server: " + e.getMessage());
        }
        return response;
    }
}
