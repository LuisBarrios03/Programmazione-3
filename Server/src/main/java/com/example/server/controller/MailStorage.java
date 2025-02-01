package com.example.server.controller;

import com.example.server.model.Email;
import com.example.server.model.MailBox;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MailStorage {
    private static final String storagePath = "com/example/server/storage";

    static {
        File storage = new File(storagePath);
        if (!storage.exists()) {
            storage.mkdir();
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

    public static synchronized List<Email> loadMailBox(String account) {
        String filePath = storagePath + account + ".json";
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type emailListType = new TypeToken<List<Email>>() {}.getType();
            return gson.fromJson(reader, emailListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

