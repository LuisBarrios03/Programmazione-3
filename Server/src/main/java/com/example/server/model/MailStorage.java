package com.example.server.model;

import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

/**
 * La classe MailStorage gestisce la memorizzazione e il recupero delle mailbox degli utenti.
 * Utilizza una mappa concorrente per gestire i lock di lettura/scrittura per ogni account email.
 */
public class MailStorage {
    private final File directory;
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks;

    /**
     * Costruttore della classe MailStorage.
     *
     * @param directory la directory in cui memorizzare le mailbox
     */
    public MailStorage(File directory) {
        this.directory = directory;
        this.locks = new ConcurrentHashMap<>();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        createMailBoxesIfNotExist();
    }

    /**
     * Crea le mailbox per gli account richiesti se non esistono.
     */
    public void createMailBoxesIfNotExist() {
        String[] requiredAccounts = {"alessio@notamail.com", "luis@notamail.com", "gigi@notamail.com"};

        for (String account : requiredAccounts) {
            File accountFile = new File(directory, account + ".bin");
            if (!accountFile.exists()) {
                try {
                    accountFile.createNewFile();
                    System.out.println("File creato per: " + account);
                } catch (IOException e) {
                    throw new RuntimeException("Errore nella creazione del file per l'account: " + account, e);
                }
            }
        }
    }

    /**
     * Ottiene il lock di lettura/scrittura per un account email.
     *
     * @param account l'account email per cui ottenere il lock
     * @return il lock di lettura/scrittura per l'account
     */
    private ReadWriteLock getLockForAccount(String account) {
        locks.putIfAbsent(account, new ReentrantReadWriteLock());
        return locks.get(account);
    }

    /**
     * Salva una mailbox su disco.
     *
     * @param mailbox la mailbox da salvare
     * @throws IOException se si verifica un errore durante il salvataggio
     */
    public void saveMailBox(MailBox mailbox) throws IOException {
        String account = mailbox.getAccount();
        ReadWriteLock lock = getLockForAccount(account);
        lock.writeLock().lock();
        try {
            File file = new File(directory, account + ".bin");
            if (!file.exists()) {
                throw new FileNotFoundException("Mailbox non trovata per l'account: " + account);
            }
            mailbox.serialize(file);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Carica una mailbox da disco.
     *
     * @param email l'email dell'account per cui caricare la mailbox
     * @return la mailbox caricata
     */
    public MailBox loadMailBox(String email) {
        File mailBoxFile = new File(directory, email + ".bin");

        if (!mailBoxFile.exists()) {
            System.out.println("File per la mailbox " + email + " non trovato.");
            return new MailBox(email);
        }

        if (mailBoxFile.length() == 0) {
            System.out.println("File vuoto per la mailbox: " + email);
            return new MailBox(email);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mailBoxFile))) {
            return (MailBox) ois.readObject();
        } catch (EOFException e) {
            System.out.println("File vuoto o corrotto per la mailbox: " + email);
            return new MailBox(email);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Errore durante la deserializzazione della mailbox per: " + email);
            e.printStackTrace();
            return new MailBox(email);
        }
    }

    /**
     * Verifica se un'email è registrata.
     *
     * @param account l'account email da verificare
     * @return true se l'email è registrata, false altrimenti
     */
    public boolean isRegisteredEmail(String account) {
        return new File(directory, account + ".bin").exists();
    }
}