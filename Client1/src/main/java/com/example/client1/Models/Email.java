package com.example.client1.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    // Proprietà per la selezione nella TableView
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public Email(String id, String sender, List<String> recipients, String subject, String body, String date) {
        // Se l'id passato è nullo, ne genera uno nuovo
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public Email() {
    }

    // Getters
    public String getId() { return id; }
    public String getSender() { return sender; }
    public List<String> getRecipients() { return recipients; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getDate() { return date; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setSender(String sender) { this.sender = sender; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setRecipients(List<String> recipients) { this.recipients = recipients; }
    public void setBody(String body) { this.body = body; }
    public void setDate(String date) { this.date = date; }

    // Proprietà per il binding della selezione
    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatari: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id) &&
                Objects.equals(sender, email.sender) &&
                Objects.equals(recipients, email.recipients) &&
                Objects.equals(subject, email.subject) &&
                Objects.equals(body, email.body) &&
                Objects.equals(date, email.date);
    }
}
