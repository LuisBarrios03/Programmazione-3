package com.example.server;

import java.util.Date;
import java.util.List;

public class Email {
    private static int idCounter = 0;
    private final int id;
    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String body;
    private final Date date;

    public Email(String sender, List<String> recipients, String subject, String body) {
        this.id = ++idCounter;
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = new Date();
    }

    public int getId() {
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

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nFrom: " + sender + "\nTo: " + recipients + "\nSubject: " + subject + "\nDate: " + date + "\n\n" + body;
    }
}
