package com.example.server.model;

import java.io.*;
import java.util.*;

/**
 * @brief Rappresenta una casella di posta elettronica per un account specifico.
 * @details Implementa Serializable per supportare la serializzazione.
 */
public class MailBox implements Serializable {
    /** @brief Account associato alla casella di posta */
    private final String account;
    /** @brief Elenco delle email ricevute */
    private final List<Email> emails;

    /**
     * @brief Costruttore della classe MailBox.
     * @param account L'account associato alla casella di posta.
     */
    public MailBox(String account) {
        this.account = account;
        this.emails = new ArrayList<>(); /// Inizializza la lista delle email
    }

    /**
     * @brief Deserializza un oggetto MailBox da un file.
     * @param file Il file sorgente.
     * @return L'oggetto MailBox deserializzato.
     * @throws IOException Errore durante la lettura.
     * @throws ClassNotFoundException Classe non trovata.
     */
    public static MailBox deserialize(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MailBox) ois.readObject(); /// Legge e restituisce l'oggetto
        }
    }

    /**
     * @brief Serializza l'oggetto MailBox in un file.
     * @param file Il file destinazione.
     * @throws IOException Errore durante la scrittura.
     */
    public void serialize(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this); /// Scrive l'oggetto nel file
        }
    }

    /** @brief Restituisce l'account associato. */
    public String getAccount() { return account; }

    /**
     * @brief Invia un'email aggiungendola alla casella.
     * @param email L'email da aggiungere.
     */
    public synchronized void sendEmail(Email email) {
        emails.add(email); /// Aggiunge l'email alla lista
    }

    /**
     * @brief Rimuove un'email dalla casella usando l'ID.
     * @param id L'ID dell'email.
     * @return true se rimossa, false altrimenti.
     */
    public synchronized boolean removeEmail(String id) {
        return emails.removeIf(email -> email.getId().equals(id)); /// Rimuove l'email corrispondente
    }

    /** @brief Restituisce la lista completa delle email. */
    public List<Email> getEmails() { return emails; }
}
