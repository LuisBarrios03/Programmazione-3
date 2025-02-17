package com.example.client1.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * La classe Email rappresenta un'email con mittente, destinatari, oggetto, corpo e data.
 * Implementa l'interfaccia Comparable per consentire l'ordinamento delle email in base alla data.
 */
public class Email implements Comparable<Email> {

    private final String id;
    private final String sender;
    private final List<String> recipients;
    private final String subject;
    private final String body;
    private final LocalDateTime date;
    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    /**
     * Costruttore della classe Email.
     *
     * @param id         l'ID dell'email, se nullo o vuoto viene generato un UUID casuale
     * @param sender     il mittente dell'email
     * @param recipients la lista dei destinatari dell'email
     * @param subject    l'oggetto dell'email
     * @param body       il corpo dell'email
     * @param date       la data di invio dell'email
     */
    public Email(String id, String sender, List<String> recipients, String subject, String body, LocalDateTime date) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    /**
     * Restituisce l'ID dell'email.
     *
     * @return l'ID dell'email
     */
    public String getId() { return id; }

    /**
     * Restituisce il mittente dell'email.
     *
     * @return il mittente dell'email
     */
    public String getSender() { return sender; }

    /**
     * Restituisce la lista dei destinatari dell'email.
     *
     * @return la lista dei destinatari dell'email
     */
    public List<String> getRecipients() { return recipients; }

    /**
     * Restituisce l'oggetto dell'email.
     *
     * @return l'oggetto dell'email
     */
    public String getSubject() { return subject; }

    /**
     * Restituisce il corpo dell'email.
     *
     * @return il corpo dell'email
     */
    public String getBody() { return body; }

    /**
     * Restituisce la data di invio dell'email.
     *
     * @return la data di invio dell'email
     */
    public LocalDateTime getDate() { return date; }

    /**
     * Restituisce la proprietà selected dell'email.
     *
     * @return la proprietà selected dell'email
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Verifica se l'email è selezionata.
     *
     * @return true se l'email è selezionata, false altrimenti
     */
    public boolean isSelected() {
        return selected.get();
    }

    /**
     * Imposta lo stato di selezione dell'email.
     *
     * @param selected lo stato di selezione dell'email
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * Restituisce una rappresentazione in formato stringa dell'email.
     *
     * @return una stringa che rappresenta l'email
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatari: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    /**
     * Verifica se l'oggetto specificato è uguale a questa email.
     *
     * @param o l'oggetto da confrontare
     * @return true se l'oggetto specificato è uguale a questa email, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id) &&
                Objects.equals(sender, email.sender) &&
                Objects.equals(recipients, email.recipients) &&
                Objects.equals(subject, email.subject) &&
                Objects.equals(body, email.body) &&
                Objects.equals(date, email.date);
    }

    /**
     * Confronta questa email con un'altra email in base alla data.
     *
     * @param o l'email da confrontare
     *         precedente, uguale o successiva all'altra email
     */
    @Override
    public int compareTo(Email o) {
        return o.date.compareTo(date);
    }
}