package com.example.server.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Email implements Serializable {
    @Expose private final String id;
    @Expose private final String sender;
    @Expose private final List<String> recipients;
    @Expose private final String subject;
    @Expose private final String body;
    @Expose private final LocalDateTime date;


    public Email(String id, String sender, List<String> recipients, String subject, String body, LocalDateTime date) {
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
    public LocalDateTime getDate() { return date; }


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
