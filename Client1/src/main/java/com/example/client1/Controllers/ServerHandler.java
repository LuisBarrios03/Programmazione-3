package com.example.client1.Controllers;

import com.example.client1.Models.Email;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        try (
                Socket socket = new Socket(serverAddress, serverPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Socket in uso: " + socket);
            out.println(data);

            String response = in.readLine();
            receiveMessage(response);

            if (response != null) {
                socket.close();
                return new JSONObject(response);
            } else {
                throw new IOException("No response from server.");
            }
        }
    }

    public void receiveMessage(String responseString) {
        JSONObject response = new JSONObject(responseString);
        if (response.getString("status").equals("OK")) {
            System.out.println("Operazione completata con successo: " + response.getString("message"));
        } else {
            System.err.println("Errore durante l'operazione: " + response.getString("message"));
        }
    }


    public boolean tryConnection(){
        try {
            JSONObject data = new JSONObject().put("data", new JSONObject());
            JSONObject response = sendCommand(data.put("action", "PING"));
            return response.getString("status").equals("OK");
        } catch (Exception e) {
           System.err.println("Errore nella connessione al server: " + e.getMessage());
              return false;
        }
    }

    public List <Email> createInbox (JSONObject data){
        JSONArray mailList = data.getJSONArray("MailList");
        List<Email> mailListConverted = new ArrayList<>();
        for (int i = 0; i < mailList.length(); i++){
            JSONObject mail = mailList.getJSONObject(i);

            List<String> recipients = new ArrayList<>();
            if (!mail.isNull("recipients")){
                JSONArray recipientsArray = mail.getJSONArray("recipients");
                for (int j = 0; j < recipientsArray.length(); j++){
                    recipients.add(recipientsArray.getString(j));
                }
            }
            Email email = new Email (
                    mail.getString("id"),
                    mail.getString("sender"),
                    recipients,
                    mail.getString("subject"),
                    mail.getString("body"),
                    LocalDateTime.parse(mail.getString("timestamp"), DateTimeFormatter.ISO_DATE_TIME).toString()
            );
            mailListConverted.add(email);
        }
        return mailListConverted;
    }

    public String getLastId(List<Email> emails){
        int lastId = 0;
        for (Email email : emails){
            int id = Integer.parseInt(email.getid());
            if (id > lastId){
                lastId = id;
            }
        }

        return "" + lastId;
    }
}

