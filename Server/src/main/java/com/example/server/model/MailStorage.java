package com.example.server.model;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MailStorage {
    private final File directory;
    private final ConcurrentHashMap<String, ReadWriteLock> locks;

    public MailStorage(File directory) {
        this.directory = directory;
        this.locks = new ConcurrentHashMap<>();
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    //Restituisce un lock specifico per un account
    private ReadWriteLock getLockForAccount(String account) {
        locks.putIfAbsent(account, new ReentrantReadWriteLock());
        return locks.get(account);
    }

    //Salva la MailBox sul file "account".bin
    public void saveMailBox(MailBox mailbox) throws IOException {
        String account = mailbox.getAccount();
        ReadWriteLock lock = getLockForAccount(account);
        lock.writeLock().lock();
        try {
            File file = new File(directory, account + ".bin");
            mailbox.serialize(file);
        } catch (IOException e) {
            System.err.println("Errore durante la serializzazione della MailBox: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    //Carica la MailBox dal file "account".bin
    public MailBox loadMailBox(String account) throws IOException, ClassNotFoundException {
        ReadWriteLock lock = getLockForAccount(account);
        lock.readLock().lock();
        try {
            File file = new File(directory, account + ".bin");
            if (!file.exists()) {
                return null;
            } else {
                return MailBox.deserialize(file);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore durante la deserializzazione della MailBox: " + e.getMessage());
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
}

