package com.example.server.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Email implements Serializable {
    @Expose private String id;
    @Expose private String sender;
    @Expose private List<String> recipients;
    @Expose private String subject;
    @Expose private String body;
    @Expose private String date;

    public Email() {
    }

    public Email(String id, String sender, List<String> recipients, String subject, String body, String date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public List<String> getRecipients() { return recipients; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getDate() { return date; }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatario: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id) &&
                Objects.equals(sender, email.sender) &&
                Objects.equals(recipients, email.recipients) &&
                Objects.equals(subject, email.subject) &&
                Objects.equals(body, email.body) &&
                Objects.equals(date, email.date);
    }

    // Converti l'email in JsonObject (Serializzazione) usando Gson
    public JsonObject toJson() {
        Gson gson = new Gson();
        return gson.toJsonTree(this).getAsJsonObject();
    }

    // Crea un'istanza di Email da un JsonObject (Deserializzazione) usando Gson
    public static Email fromJson(JsonObject emailJson) {
        Gson gson = new Gson();
        return gson.fromJson(emailJson, Email.class);
    }
}
