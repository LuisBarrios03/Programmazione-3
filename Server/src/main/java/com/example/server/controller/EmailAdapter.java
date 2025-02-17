package com.example.server.controller;

import com.example.server.model.Email;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe adattatore per la serializzazione e deserializzazione di oggetti Email con Gson.
 * @details Questa classe implementa le interfacce `JsonDeserializer<Email>` e `JsonSerializer<Email>`
 * per permettere la conversione di oggetti Email in formato JSON e viceversa.
 */
public class EmailAdapter implements JsonDeserializer<Email>, JsonSerializer<Email> {

    /**
     * Deserializza un oggetto JSON in un'istanza di Email.
     * @param json l'elemento JSON da deserializzare
     * @param typeOfT il tipo dell'oggetto da deserializzare
     * @param context il contesto di deserializzazione
     * @return l'oggetto Email deserializzato
     * @throws JsonParseException se si verifica un errore durante la deserializzazione
     * @details Questo metodo converte un oggetto JSON in un'istanza della classe `Email`,
     * facendo attenzione a trattare correttamente i vari campi come `id`, `sender`, `recipients`, ecc.
     */
    @Override
    public Email deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : null;  /// Estrae l'ID
        String sender = jsonObject.has("sender") ? jsonObject.get("sender").getAsString() : null;  /// Estrae il mittente
        List<String> recipients = new ArrayList<>();  /// Crea una lista vuota per i destinatari
        if (jsonObject.has("recipients")) {  /// Controlla se esistono destinatari nel JSON
            JsonArray recipientsArray = jsonObject.getAsJsonArray("recipients");  /// Estrae la lista di destinatari
            for (JsonElement recipientElement : recipientsArray) {
                recipients.add(recipientElement.getAsString());  /// Aggiunge ogni destinatario alla lista
            }
        }
        String subject = jsonObject.has("subject") ? jsonObject.get("subject").getAsString() : null;  /// Estrae l'oggetto
        String body = jsonObject.has("body") ? jsonObject.get("body").getAsString() : null;  /// Estrae il corpo
        String date = jsonObject.get("date").getAsString();  /// Estrae la data come stringa

        return new Email(id, sender, recipients, subject, body, LocalDateTime.parse(date));  /// Restituisce una nuova istanza di Email
    }

    /**
     * Serializza un'istanza di Email in un oggetto JSON.
     * @param src l'oggetto Email da serializzare
     * @param typeOfSrc il tipo dell'oggetto da serializzare
     * @param context il contesto di serializzazione
     * @return l'elemento JSON serializzato
     * @details Questo metodo converte un'istanza della classe `Email` in un oggetto JSON,
     * gestendo la conversione dei campi come `id`, `sender`, `recipients`, ecc.
     */
    @Override
    public JsonElement serialize(Email src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", src.getId());  /// Aggiunge l'ID dell'email al JSON
        jsonObject.addProperty("sender", src.getSender());  /// Aggiunge il mittente dell'email al JSON
        JsonArray recipientsArray = new JsonArray();  /// Crea un array JSON per i destinatari
        for (String recipient : src.getRecipients()) {
            recipientsArray.add(recipient);  /// Aggiunge ogni destinatario all'array JSON
        }
        jsonObject.add("recipients", recipientsArray);  /// Aggiunge la lista dei destinatari al JSON
        jsonObject.addProperty("subject", src.getSubject());  /// Aggiunge l'oggetto dell'email al JSON
        jsonObject.addProperty("body", src.getBody());  /// Aggiunge il corpo dell'email al JSON
        jsonObject.addProperty("date", src.getDate().toString());  /// Aggiunge la data dell'email al JSON

        return jsonObject;  /// Restituisce l'oggetto JSON serializzato
    }
}
