package com.example.client1.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * La classe Client rappresenta un cliente di posta elettronica con informazioni come account,
 * mittente, destinatari, oggetto, corpo del messaggio, data, stato della connessione e una lista di email.
 */
public class Client {
    private final SimpleStringProperty account;  /// Proprietà per l'account del cliente
    private final SimpleStringProperty sender;  /// Proprietà per il mittente dell'email
    private final ListProperty<String> recipients;  /// Proprietà per la lista dei destinatari
    private final SimpleStringProperty subject;  /// Proprietà per l'oggetto dell'email
    private final SimpleStringProperty body;  /// Proprietà per il corpo dell'email
    private final SimpleStringProperty date;  /// Proprietà per la data dell'email
    private final SimpleStringProperty connection;  /// Proprietà per lo stato di connessione
    private final ListProperty<Email> mailList;  /// Proprietà per la lista delle email

    /**
     * Costruttore della classe Client che inizializza tutte le proprietà con valori di default.
     */
    public Client() {
        this.account = new SimpleStringProperty();
        this.sender = new SimpleStringProperty();
        this.recipients = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.subject = new SimpleStringProperty();
        this.body = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.connection = new SimpleStringProperty();
        this.mailList = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /// Getter e setter per le proprietà

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

    /**
     * Aggiunge una lista di email alla lista corrente di email.
     *
     * @param newMailList la nuova lista di email da aggiungere
     */
    public void updateMailList(List<Email> newMailList) {
        mailList.addAll(newMailList);
    }

    /**
     * Resetta tutte le proprietà del client.
     * Resetta le proprietà stringa e svuota la lista delle email.
     */
    public void resetClient() {
        /// Reset delle proprietà stringa
        this.account.set("");  /// Resetta l'account
        this.connection.set("OFF");  /// Resetta lo stato della connessione

        /// Reset della lista di email
        this.mailList.clear();  /// Svuota la lista di email
    }
}
