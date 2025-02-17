package com.example.server.model;

import com.google.gson.annotations.Expose;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @brief La classe Email rappresenta un'email completa di mittente, destinatari, oggetto, corpo e data.
 * @details Implementa Serializable per supportare la persistenza e l'integrazione con Gson per il formato JSON.
 */
public class Email implements Serializable {
    @Expose private final String id;                /// ID univoco dell'email
    @Expose private final String sender;            /// Mittente dell'email
    @Expose private final List<String> recipients;  /// Lista dei destinatari
    @Expose private final String subject;           /// Oggetto dell'email
    @Expose private final String body;              /// Corpo del messaggio
    @Expose private final LocalDateTime date;       /// Data e ora dell'invio

    /**
     * @brief Costruttore principale della classe Email.
     * @param id Identificatore univoco (generato automaticamente).
     * @param sender Mittente dell'email.
     * @param recipients Lista dei destinatari.
     * @param subject Oggetto dell'email.
     * @param body Contenuto del messaggio.
     * @param date Data e ora di invio.
     */
    public Email(String id, String sender, List<String> recipients, String subject, String body, LocalDateTime date) {
        this.id = UUID.randomUUID().toString();         /// Genera un UUID univoco per ogni email
        this.sender = sender;
        this.recipients = List.copyOf(recipients);     /// Crea una copia immutabile della lista
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    /** @return Restituisce l'ID dell'email. */
    public String getId() { return id; }

    /** @return Restituisce il mittente dell'email. */
    public String getSender() { return sender; }

    /** @return Restituisce la lista dei destinatari. */
    public List<String> getRecipients() { return recipients; }

    /** @return Restituisce l'oggetto dell'email. */
    public String getSubject() { return subject; }

    /** @return Restituisce il corpo del messaggio. */
    public String getBody() { return body; }

    /** @return Restituisce la data di invio. */
    public LocalDateTime getDate() { return date; }

    /**
     * @brief Genera una rappresentazione testuale dell'email.
     * @return Stringa contenente i dettagli dell'email.
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatari: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    /**
     * @brief Confronta questa email con un altro oggetto.
     * @param o Oggetto da confrontare.
     * @return true se le email coincidono, false altrimenti.
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
