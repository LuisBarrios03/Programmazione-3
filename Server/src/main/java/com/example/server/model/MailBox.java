package com.example.server.model;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MailBox implements Serializable {
    private final String account;
    private final List<Email> emails;

    public MailBox(String account) {
        this.account = account;
        this.emails = new ArrayList<>();
    }

    // Deserializza la MailBox da un file binario
    public static MailBox deserialize(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MailBox) ois.readObject();
        }
    }

    // Serializza la MailBox su un file binario
    public void serialize(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }


    public String getAccount() {
        return account;
    }

    // Aggiunge una email alla MailBox
    public synchronized void sendEmail(Email email) {
        emails.add(email);
    }

    // Rimuove una email dato il suo id e restituisce true in caso di successo
    public synchronized boolean removeEmail(String id) {
        return emails.removeIf(email -> email.getId().equals(id));
    }

    // Restituisce tutte le email presenti nella MailBox
    public List<Email> getEmails() {
        return emails;
    }
}
