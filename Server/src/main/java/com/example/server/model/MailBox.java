package com.example.server.model;

import java.util.ArrayList;
import java.util.List;

public class MailBox {
    private final String account;
    private final List<Email> emails = new ArrayList<>();


    public MailBox(String account) {
        this.account = account;
    }

    public synchronized void addEmail(Email email) {
        emails.add(email);
    }

    public List<Email> getAllMails() {
        return new ArrayList<>(emails);
    }

    public Email getEmailById(String id) {
        for (Email email : emails) {
            if (email.getId().equals(id)) {
                return email;
            }
        }
        return null;
    }

    public Email getEmailByUser(String user) {
        for (Email email : emails) {
            if (email.getSender().equals(user)) {
                return email;
            }
        }
        return null;
    }

    public String toString() {
        return "MailBox{" + "account='" + account + '\'' + ", emails=" + emails + '}';
    }
}
