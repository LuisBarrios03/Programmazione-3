package com.example.server.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Email {



    private final String id;
    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String body;
    private final String date;

    public Email(String sender, List<String> recipients, String subject, String body, String date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public String getId() {
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


    @Override
    public String toString() {
        return "ID: " + id + "\nFrom: " + sender + "\nTo: " + recipients + "\nSubject: " + subject + "\nDate: " + date + "\n\n" + body;
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

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, recipients, subject, body, date);
    }

}
