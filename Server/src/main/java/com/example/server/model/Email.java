package com.example.server.model;

import org.json.JSONObject;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Email implements Serializable {

    private String id;
    private String sender;
    private List<String> recipients;
    private String subject;
    private String body;
    private String date;

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

    // Converti l'email in JSONObject (Serializzazione)
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("sender", sender);
        json.put("recipients", recipients);
        json.put("subject", subject);
        json.put("body", body);
        json.put("date", date);
        return json;
    }

    // Crea un'istanza di Email da un JSONObject (Deserializzazione)
    public static Email fromJson(JSONObject emailJson) {
        return new Email(
                emailJson.getString("id"),
                emailJson.getString("sender"),
                emailJson.getJSONArray("recipients").toList().stream().map(Object::toString).toList(),
                emailJson.getString("subject"),
                emailJson.getString("body"),
                emailJson.getString("date")
        );
    }
}
