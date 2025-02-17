package com.example.server.model;

import java.io.*;
import java.util.*;

/**
 * La classe MailBox rappresenta una casella di posta elettronica per un account specifico.
 * Implementa l'interfaccia Serializable per consentire la serializzazione degli oggetti MailBox.
 */
public class MailBox implements Serializable {
    private final String account;
    private final List<Email> emails;

    /**
     * Costruttore della classe MailBox.
     *
     * @param account L'account associato alla casella di posta.
     */
    public MailBox(String account) {
        this.account = account;
        this.emails = new ArrayList<>();
    }

    /**
     * Deserializza un oggetto MailBox da un file.
     *
     * @param file Il file da cui deserializzare l'oggetto MailBox.
     * @return L'oggetto MailBox deserializzato.
     * @throws IOException Se si verifica un errore di I/O durante la deserializzazione.
     * @throws ClassNotFoundException Se la classe MailBox non viene trovata.
     */
    public static MailBox deserialize(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (MailBox) ois.readObject();
        }
    }

    /**
     * Serializza l'oggetto MailBox in un file.
     *
     * @param file Il file in cui serializzare l'oggetto MailBox.
     * @throws IOException Se si verifica un errore di I/O durante la serializzazione.
     */
    public void serialize(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    /**
     * Restituisce l'account associato alla casella di posta.
     *
     * @return L'account associato alla casella di posta.
     */
    public String getAccount() {
        return account;
    }

    /**
     * Invia un'email aggiungendola alla casella di posta.
     *
     * @param email L'email da inviare.
     */
    public synchronized void sendEmail(Email email) {
        emails.add(email);
    }

    /**
     * Rimuove un'email dalla casella di posta in base al suo ID.
     *
     * @param id L'ID dell'email da rimuovere.
     * @return true se l'email è stata rimossa, altrimenti false.
     */
    public synchronized boolean removeEmail(String id) {
        return emails.removeIf(email -> email.getId().equals(id));
    }

    /**
     * Restituisce la lista delle email nella casella di posta.
     *
     * @return La lista delle email nella casella di posta.
     */
    public List<Email> getEmails() {
        return emails;
    }
}