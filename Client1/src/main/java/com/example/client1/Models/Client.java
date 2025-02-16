package com.example.client1.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class Client {
    private final SimpleStringProperty account;
    private final SimpleStringProperty sender;
    private final ListProperty<String> recipients;
    private final SimpleStringProperty subject;
    private final SimpleStringProperty body;
    private final SimpleStringProperty date;
    private final SimpleStringProperty connection;
    private final ListProperty<Email> mailList;
    private final BooleanProperty selected;

    public Client() {
        this.account = new SimpleStringProperty();
        this.sender = new SimpleStringProperty();
        this.recipients = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.subject = new SimpleStringProperty();
        this.body = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.connection = new SimpleStringProperty();
        this.mailList = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.selected = new SimpleBooleanProperty(false);
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

    public ObservableList<String> getRecipients() {
        return recipients.get();
    }

    public ListProperty<String> recipientsProperty() {
        return recipients;
    }

    public void setRecipients(List<String> newRecipients) {
        this.recipients.set(FXCollections.observableArrayList(newRecipients));
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

    public ObservableList<Email> getMailList() {
        return mailList.get();
    }

    public ListProperty<Email> mailListProperty() {
        return mailList;
    }

    public void setMailList(List<Email> newMailList) {
        this.mailList.set(FXCollections.observableArrayList(newMailList));
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
