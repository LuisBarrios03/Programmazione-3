package com.example.server.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MailBox implements Serializable{
    private  String account;
    private  List<Email> emails;


    public static MailBox deserialize(File file){

    }

}
/*

    public MailBox(String account, List<Email> emails) {
        this.account = account;
        this.emails= emails;
    }

    */
/*public synchronized void addEmail(Email email) {
        emails.add(email);
        MailStorage.saveMailbox(this);
    }*//*


    public static synchronized List<Email> loadMailBox(String account) {
        String filePath = storagePath + account + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type emailListType = new TypeToken<List<Email>>() {}.getType();
            return gson.fromJson(reader,emailListType );
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getAccount() {
        return account;
    }

    public List<Email> getAllMails() {
        JSONArray array = this.loadMessages(account);
        List<Email> emails = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String sender = obj.getString("sender");
            String subject = obj.getString("subject");
            String body = obj.getString("body");
            String date = obj.getString("date");
            JSONArray recipients = obj.getJSONArray("recipients");
            List<String> recipientsList = new ArrayList<>();
            for(int j = 0; j < recipients.length(); j++) {
                recipientsList.add(recipients.getString(j));
            }
            Email email = new Email(sender, recipientsList, subject, body, date, "cause");
            emails.add(email);
        }
        this.emails = emails;
        return emails;
    }
    //test

    public synchronized JSONArray loadMessages(String filename) {
        JSONArray messages = new JSONArray();
        try (BufferedReader reader = new BufferedReader(new FileReader(storagePath + filename + ".json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Parse del contenuto JSON
            JSONArray jsonArray = new JSONArray(jsonContent.toString());

            // Aggiungi ogni oggetto JSON al JSONArray finale
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mailJson = jsonArray.getJSONObject(i);

                // Aggiungi il JSONObject direttamente al JSONArray finale
                messages.put(mailJson);
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
        return messages;
    }

    //fine test

    */
/*
        public Email getEmailById(String id) {
            for (Email email : emails) {
                if (email.getId().equals(id)) {
                    return email;
                }
            }
            return null;
        }

        public Email getEmailByUser(String user) {
            for (Email email : emails) {
                if (email.getSender().equals(user)) {
                    return email;
                }
            }
            return null;
        }
    *//*


    public String toString() {
        return "MailBox{" + "account='" + account + '\'' + ", emails=" + emails + '}';
    }
}
