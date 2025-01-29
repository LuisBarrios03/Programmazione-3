package com.example.server;

import java.util.ArrayList;
import java.util.List;

public class MailBox {
    private final String account;
    private final List<Email> emails = new ArrayList<>();


    public MailBox(String account) {
        this.account = account;
    }

    public synchronized void receiveEmail(Email email) {
        emails.add(email);
    }

    public synchronized List<Email> getEmails() {
        return new ArrayList<>(emails);
    }
}
