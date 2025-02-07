package com.example.server.model;

import java.util.List;

public class MailBox {
    private final String account;
    private final List<Email> emails;


    public MailBox(String account, List<Email> emails) {
        this.account = account;
        this.emails= emails;
    }

    /*public synchronized void addEmail(Email email) {
        emails.add(email);
        MailStorage.saveMailbox(this);
    }*/

    public String getAccount() {
        return account;
    }

    public List<Email> getAllMails() {
        return emails;
    }
    /*
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
    */
    public String toString() {
        return "MailBox{" + "account='" + account + '\'' + ", emails=" + emails + '}';
    }
}
