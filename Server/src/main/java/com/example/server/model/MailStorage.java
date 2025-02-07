package com.example.server.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MailStorage {

    private static final String storagePath = "data/";
    public  static void createStorage() {
        File storage = new File(storagePath);
        if (!storage.exists()) {
            if (storage.mkdirs()) {
                System.out.println("Cartella creata con successo!");
            } else {
                System.err.println("Errore nella creazione della cartella!");
            }
        }else{
            System.out.println("Cartella già esistente!");
        }

    }

    public static void saveMailbox(MailBox mailbox) {
        String filePath = storagePath + mailbox.getAccount() + ".json";
        try (Writer writer = new FileWriter(filePath)) {
            Gson gson = new Gson();
            writer.write(gson.toJson(mailbox.getAllMails()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

