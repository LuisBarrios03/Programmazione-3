package com.example.client1.Models;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Email implements Serializable {

    private  String id;
    private  String sender;
    private  List<String> recipients;
    private  String subject;
    private  String body;
    private  String date;

    public Email(String id, String sender, List<String> recipients, String subject, String body, String date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getid() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public void setId(String account){
        this.id = account;
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
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatario: " + recipients + "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return
                Objects.equals(id, email.id) && Objects.equals(sender, email.sender)
                        && Objects.equals(recipients, email.recipients)
                        && Objects.equals(subject, email.subject)
                        && Objects.equals(body, email.body)
                        && Objects.equals(date, email.date);
    }
}
