package com.example.client1.Models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * La classe Email rappresenta un'email con il mittente, destinatari, oggetto, corpo e data di invio.
 * Implementa l'interfaccia Comparable per consentire l'ordinamento delle email in base alla data di invio.
 */
public class Email implements Comparable<Email> {

    private final String id;  /// ID univoco dell'email
    private final String sender;  /// Mittente dell'email
    private final List<String> recipients;  /// Lista dei destinatari
    private final String subject;  /// Oggetto dell'email
    private final String body;  /// Corpo dell'email
    private final LocalDateTime date;  /// Data di invio dell'email
    private final BooleanProperty selected = new SimpleBooleanProperty(false);  /// Proprietà che rappresenta se l'email è selezionata

    /**
     * Costruttore della classe Email.
     * Se l'ID è nullo o vuoto, viene generato un UUID casuale.
     *
     * @param id         l'ID dell'email
     * @param sender     il mittente dell'email
     * @param recipients la lista dei destinatari dell'email
     * @param subject    l'oggetto dell'email
     * @param body       il corpo dell'email
     * @param date       la data di invio dell'email
     */
    public Email(String id, String sender, List<String> recipients, String subject, String body, LocalDateTime date) {
        this.id = (id != null && !id.isEmpty()) ? id : UUID.randomUUID().toString();  /// Genera un ID unico se non specificato
        this.sender = sender;
        this.recipients = List.copyOf(recipients);  /// Crea una copia immutabile della lista dei destinatari
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
     * @return la lista dei destinatari
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
     * @return la data dell'email
     */
    public LocalDateTime getDate() { return date; }

    /**
     * Restituisce la proprietà "selected" dell'email, che rappresenta se l'email è selezionata.
     *
     * @return la proprietà BooleanProperty "selected"
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
     * @param selected lo stato di selezione da impostare
     */
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * Restituisce una rappresentazione in formato stringa dell'email.
     * La rappresentazione include l'ID, mittente, destinatari, oggetto, data e corpo dell'email.
     *
     * @return una stringa che rappresenta l'email
     */
    @Override
    public String toString() {
        return "ID: " + id + "\nMittente: " + sender + "\nDestinatari: " + recipients +
                "\nOggetto: " + subject + "\nData: " + date + "\n\nCorpo: " + body;
    }

    /**
     * Confronta questa email con un'altra email per determinare se sono uguali.
     * Le email sono considerate uguali se hanno lo stesso ID, mittente, destinatari, oggetto, corpo e data.
     *
     * @param o l'oggetto da confrontare
     * @return true se l'oggetto è uguale all'email corrente, false altrimenti
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  /// Se l'oggetto è lo stesso, sono uguali
        if (!(o instanceof Email)) return false;  /// Se l'oggetto non è un'istanza di Email, sono diversi
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
     * La comparazione avviene in ordine decrescente, in modo che le email più recenti vengano ordinate per prime.
     *
     * @param o l'email da confrontare
     * @return un valore negativo, zero o positivo a seconda che questa email sia precedente, uguale o successiva all'altra email
     */
    @Override
    public int compareTo(Email o) {
        return o.date.compareTo(date);  /// Ordinamento basato sulla data in ordine decrescente
    }
}
