package com.example.server.model;

import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

/**
 * @brief Gestisce la memorizzazione e il recupero delle mailbox degli utenti.
 * Utilizza una mappa concorrente per i lock di lettura/scrittura per ogni account.
 */
public class MailStorage {
    /** @brief Directory per la memorizzazione delle mailbox */
    private final File directory;
    /** @brief Mappa concorrente per i lock di lettura/scrittura */
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks;

    /**
     * @brief Costruttore.
     * @param directory la directory per le mailbox
     */
    public MailStorage(File directory) {
        this.directory = directory;
        this.locks = new ConcurrentHashMap<>();
        if (!directory.exists()) {
            directory.mkdirs(); /// @brief Crea la directory se non esiste
        }
        createMailBoxesIfNotExist(); /// @brief Inizializza le mailbox richieste
    }

    /**
     * @brief Crea le mailbox richieste se non esistono.
     */
    public void createMailBoxesIfNotExist() {
        String[] requiredAccounts = {"alessio@notamail.com", "luis@notamail.com", "gigi@notamail.com"};
        for (String account : requiredAccounts) {
            File accountFile = new File(directory, account + ".bin");
            if (!accountFile.exists()) {
                try {
                    accountFile.createNewFile(); /// @brief Crea il file della mailbox
                    System.out.println("File creato per: " + account);
                } catch (IOException e) {
                    throw new RuntimeException("Errore nella creazione del file per: " + account, e);
                }
            }
        }
    }

    /**
     * @brief Ottiene il lock per un account.
     * @param account l'account per cui ottenere il lock
     * @return il lock di lettura/scrittura
     */
    private ReadWriteLock getLockForAccount(String account) {
        locks.putIfAbsent(account, new ReentrantReadWriteLock()); /// @brief Aggiunge il lock se assente
        return locks.get(account);
    }

    /**
     * @brief Salva una mailbox su disco.
     * @param mailbox la mailbox da salvare
     * @throws IOException se si verifica un errore
     */
    public void saveMailBox(MailBox mailbox) throws IOException {
        String account = mailbox.getAccount();
        ReadWriteLock lock = getLockForAccount(account);
        lock.writeLock().lock(); /// @brief Blocca la scrittura
        try {
            File file = new File(directory, account + ".bin");
            if (!file.exists()) {
                throw new FileNotFoundException("Mailbox non trovata per: " + account);
            }
            mailbox.serialize(file); /// @brief Serializza la mailbox
        } finally {
            lock.writeLock().unlock(); /// @brief Rilascia il lock
        }
    }

    /**
     * @brief Carica una mailbox da disco.
     * @param email l'email dell'account
     * @return la mailbox caricata
     */
    public MailBox loadMailBox(String email) {
        File mailBoxFile = new File(directory, email + ".bin");
        if (!mailBoxFile.exists() || mailBoxFile.length() == 0) {
            System.out.println("File vuoto o inesistente per: " + email);
            return new MailBox(email);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mailBoxFile))) {
            return (MailBox) ois.readObject(); /// @brief Deserializza la mailbox
        } catch (EOFException e) {
            System.out.println("File vuoto o corrotto per: " + email);
            return new MailBox(email);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Errore nella deserializzazione per: " + email);
            e.printStackTrace();
            return new MailBox(email);
        }
    }

    /**
     * @brief Verifica se un'email è registrata.
     * @param account l'account da verificare
     * @return true se esiste, false altrimenti
     */
    public boolean isRegisteredEmail(String account) {
        return new File(directory, account + ".bin").exists(); /// @brief Controlla l'esistenza del file
    }
}
