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
import java.util.UUID;

public class Email {

    private static final String storagePath = "data/";

    private final String id;
    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String body;
    private final String date;

    public Email(String sender, List<String> recipients, String subject, String body, String date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.date = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
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

    //serializzazione e deserializzazione
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public static Email fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Email.class);
    }


    @Override
    public String toString() {
        return "ID: " + id + "\nFrom: " + sender + "\nTo: " + recipients + "\nSubject: " + subject + "\nDate: " + date + "\n\n" + body;
    }
}
