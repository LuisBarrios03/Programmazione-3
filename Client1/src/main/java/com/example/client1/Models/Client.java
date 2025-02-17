package com.example.client1.Models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * La classe Client rappresenta un client di posta elettronica con varie proprietà
 * come account, mittente, destinatari, oggetto, corpo del messaggio, data, connessione e lista di email.
 */
public class Client {
    private final SimpleStringProperty account;
    private final SimpleStringProperty sender;
    private final ListProperty<String> recipients;
    private final SimpleStringProperty subject;
    private final SimpleStringProperty body;
    private final SimpleStringProperty date;
    private final SimpleStringProperty connection;
    private final ListProperty<Email> mailList;

    /**
     * Costruttore della classe Client.
     * Inizializza tutte le proprietà con valori di default.
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

    /**
     * Restituisce l'account del client.
     * @return l'account del client.
     */
    public String getAccount() {
        return account.get();
    }

    /**
     * Restituisce la proprietà account.
     * @return la proprietà account.
     */
    public SimpleStringProperty accountProperty() {
        return account;
    }

    /**
     * Imposta l'account del client.
     * @param account il nuovo account del client.
     */
    public void setAccount(String account) {
        this.account.set(account);
    }

    /**
     * Restituisce il mittente del client.
     * @return il mittente del client.
     */
    public String getSender() {
        return sender.get();
    }

    /**
     * Restituisce la proprietà sender.
     * @return la proprietà sender.
     */
    public SimpleStringProperty senderProperty() {
        return sender;
    }

    /**
     * Imposta il mittente del client.
     * @param sender il nuovo mittente del client.
     */
    public void setSender(String sender) {
        this.sender.set(sender);
    }

    /**
     * Restituisce la lista dei destinatari del client.
     * @return la lista dei destinatari del client.
     */
    public ObservableList<String> getRecipients() {
        return recipients.get();
    }

    /**
     * Restituisce la proprietà recipients.
     * @return la proprietà recipients.
     */
    public ListProperty<String> recipientsProperty() {
        return recipients;
    }

    /**
     * Imposta la lista dei destinatari del client.
     * @param newRecipients la nuova lista dei destinatari del client.
     */
    public void setRecipients(List<String> newRecipients) {
        this.recipients.set(FXCollections.observableArrayList(newRecipients));
    }

    /**
     * Restituisce l'oggetto del messaggio del client.
     * @return l'oggetto del messaggio del client.
     */
    public String getSubject() {
        return subject.get();
    }

    /**
     * Restituisce la proprietà subject.
     * @return la proprietà subject.
     */
    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    /**
     * Imposta l'oggetto del messaggio del client.
     * @param subject il nuovo oggetto del messaggio del client.
     */
    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    /**
     * Restituisce il corpo del messaggio del client.
     * @return il corpo del messaggio del client.
     */
    public String getBody() {
        return body.get();
    }

    /**
     * Restituisce la proprietà body.
     * @return la proprietà body.
     */
    public SimpleStringProperty bodyProperty() {
        return body;
    }

    /**
     * Imposta il corpo del messaggio del client.
     * @param body il nuovo corpo del messaggio del client.
     */
    public void setBody(String body) {
        this.body.set(body);
    }

    /**
     * Restituisce la data del messaggio del client.
     * @return la data del messaggio del client.
     */
    public String getDate() {
        return date.get();
    }

    /**
     * Restituisce la proprietà date.
     * @return la proprietà date.
     */
    public SimpleStringProperty dateProperty() {
        return date;
    }

    /**
     * Imposta la data del messaggio del client.
     * @param date la nuova data del messaggio del client.
     */
    public void setDate(String date) {
        this.date.set(date);
    }

    /**
     * Restituisce la connessione del client.
     * @return la connessione del client.
     */
    public String getConnection() {
        return connection.get();
    }

    /**
     * Restituisce la proprietà connection.
     * @return la proprietà connection.
     */
    public SimpleStringProperty connectionProperty() {
        return connection;
    }

    /**
     * Imposta la connessione del client.
     * @param connection la nuova connessione del client.
     */
    public void setConnection(String connection) {
        this.connection.set(connection);
    }

    /**
     * Restituisce la lista delle email del client.
     * @return la lista delle email del client.
     */
    public ObservableList<Email> getMailList() {
        return mailList.get();
    }

    /**
     * Restituisce la proprietà mailList.
     * @return la proprietà mailList.
     */
    public ListProperty<Email> mailListProperty() {
        return mailList;
    }

    /**
     * Aggiorna la lista delle email del client.
     * @param newMailList la nuova lista delle email del client.
     */
    public void updateMailList(List<Email> newMailList) {
        mailList.addAll(newMailList);
    }
}