package com.example.server.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * La classe Email rappresenta un'email con mittente, destinatari, oggetto, corpo e data.
 * Implementa l'interfaccia Serializable per consentire la serializzazione degli oggetti Email.
 */
public class Email implements Serializable {
    @Expose private final String id;
    @Expose private final String sender;
    @Expose private final List<String> recipients;
    @Expose private final String subject;
    @Expose private final String body;
    @Expose private final LocalDateTime date;

    /**
     * Costruttore della classe Email.
     *
     * @param id L'ID univoco dell'email.
     * @param sender Il mittente dell'email.
     * @param recipients La lista dei destinatari dell'email.
     * @param subject L'oggetto dell'email.
     * @param body Il corpo dell'email.
     * @param date La data di invio dell'email.
     */
    public Email(String id, String sender, List<String> recipients, String subject, String body, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.recipients = List.copyOf(recipients);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    /**
     * Restituisce l'ID dell'email.
     *
     * @return L'ID dell'email.
     */
    public String getId() { return id; }

    /**
     * Restituisce il mittente dell'email.
     *
     * @return Il mittente dell'email.
     */
    public String getSender() { return sender; }

    /**
     * Restituisce la lista dei destinatari dell'email.
     *
     * @return La lista dei destinatari dell'email.
     */
    public List<String> getRecipients() { return recipients; }

    /**
     * Restituisce l'oggetto dell'email.
     *
     * @return L'oggetto dell'email.
     */
    public String getSubject() { return subject; }

    /**
     * Restituisce il corpo dell'email.
     *
     * @return Il corpo dell'email.
     */
    public String getBody() { return body; }

    /**
     * Restituisce la data di invio dell'email.
     *
     * @return La data di invio dell'email.
     */
    public LocalDateTime getDate() { return date; }

    /**
     * Restituisce una rappresentazione in formato stringa dell'email.
     *
     * @return Una stringa che rappresenta l'email.
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatario: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    /**
     * Confronta questa email con l'oggetto specificato. Restituisce true se gli oggetti sono uguali.
     *
     * @param o L'oggetto da confrontare con questa email.
     * @return true se gli oggetti sono uguali, altrimenti false.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id) &&
                Objects.equals(sender, email.sender) &&
                Objects.equals(recipients, email.recipients) &&
                Objects.equals(subject, email.subject) &&
                Objects.equals(date, email.date);
    }
}