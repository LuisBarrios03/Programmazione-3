package com.example.server.model;

import java.io.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class MailStorage {
    private final File directory;
    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks;

    public MailStorage(File directory) {
        this.directory = directory;
        this.locks = new ConcurrentHashMap<>();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        createMailBoxesIfNotExist();
    }

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

    private ReadWriteLock getLockForAccount(String account) {
        locks.putIfAbsent(account, new ReentrantReadWriteLock());
        return locks.get(account);
    }

    public  void saveMailBox(MailBox mailbox) throws IOException {
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

    public boolean isRegisteredEmail(String account) {
        return new File(directory, account + ".bin").exists();
    }
}
