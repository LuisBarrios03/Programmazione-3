package com.example.client1.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client {
    SimpleStringProperty account;
    SimpleStringProperty sender;
    SimpleStringProperty recipients;
    SimpleStringProperty subject;
    SimpleStringProperty body;
    SimpleStringProperty date;
    SimpleStringProperty connection;
    ListProperty<Email> mailList;
    BooleanProperty selected;

    public Client() {
        this.account = new SimpleStringProperty();
        this.sender = new SimpleStringProperty();
        this.recipients = new SimpleStringProperty();
        this.subject = new SimpleStringProperty();
        this.body = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.connection = new SimpleStringProperty();
        this.mailList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.selected= new SimpleBooleanProperty(false);
    }

    public String getAccount() {
        return account.get();
    }

    public SimpleStringProperty accountProperty() {
        return account;
    }

    public void setAccount(String account) {
        this.account.set(account);
    }

    public String getSender() {
        return sender.get();
    }

    public SimpleStringProperty senderProperty() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender.set(sender);
    }

    public String getRecipients() {
        return recipients.get();
    }

    public SimpleStringProperty recipientsProperty() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients.set(recipients);
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getBody() {
        return body.get();
    }

    public SimpleStringProperty bodyProperty() {
        return body;
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getConnection() {
        return connection.get();
    }

    public SimpleStringProperty connectionProperty() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection.set(connection);
    }

    public ListProperty<Email> mailListProperty() {
        return mailList;
    }

    public void setMailList(ObservableList<Email> mailList) {
        this.mailList.set(mailList);
    }

    public ObservableList<Email> getMailList() {
        return mailList.get();
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

}

