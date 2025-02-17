package com.example.server.model;

import java.io.*;
import java.util.*;

public class MailBox implements Serializable {
    private final String account;
    private final List<Email> emails;

    public MailBox(String account) {
        this.account = account;
        this.emails = new ArrayList<>();
    }

    public static MailBox deserialize(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MailBox) ois.readObject();
        }
    }

    public void serialize(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }


    public String getAccount() {
        return account;
    }

    public synchronized void sendEmail(Email email) {
        emails.add(email);
    }

    public synchronized boolean removeEmail(String id) {
        return emails.removeIf(email -> email.getId().equals(id));
    }

    public List<Email> getEmails() {
        return emails;
    }
}
