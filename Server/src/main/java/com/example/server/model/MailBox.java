package com.example.server.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MailBox implements Serializable {
    private final String account;
    private final List<Email> emails;


    public MailBox(String account) {
        this.account = account;
        this.emails = new ArrayList<>();
    }

    //Deserializza la MailBox da un file (Lettura)
    public static MailBox deserialize(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MailBox) ois.readObject();
        }
    }

    //Serializza la MailBox su un file (Scrittura)
    public void serialize(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    // Deserializza la MailBox da un file JSON (Lettura)
    public static MailBox deserializeFromJson(File file) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
        JSONObject json = new JSONObject(content);
        MailBox mailbox = new MailBox(json.getString("account"));
        JSONArray emailArray = json.getJSONArray("emails");
        for (int i = 0; i < emailArray.length(); i++) {
            JSONObject emailJson = emailArray.getJSONObject(i);
            mailbox.emails.add(Email.fromJson(emailJson));
        }
        return mailbox;
    }

    // Serializza la MailBox su un file JSON (Scrittura)
    public void serializeToJson(File file) throws IOException {
        JSONObject json = new JSONObject();
        json.put("account", account);
        JSONArray emailArray = new JSONArray();
        for (Email email : emails) {
            emailArray.put(email.toJson());
        }
        json.put("emails", emailArray);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json.toString(4)); // Indentazione per leggibilità
        }
    }



    public String getAccount() {
        return account;
    }

    //Aggiunge una email alla MailBox
    public synchronized void sendEmail(Email email) {
            emails.add(email);
    }

    //Rimuove una email dato il suo id e restituisce true in caso di successo
    public synchronized boolean removeEmail(String id) {
        //Controlla che l'id fornito sia corretto
            return emails.removeIf(email -> email.getId().equals(id));
    }


    //Restituisce tutte le email presenti nella MailBox
    public List<Email> getEmails() {
        return emails;
    }
}

