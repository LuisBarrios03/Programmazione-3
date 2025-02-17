package com.example.client1.Controllers;

import com.example.client1.Models.Email;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmailAdapter implements JsonDeserializer<Email>, JsonSerializer<Email> {

    @Override
    public Email deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : null;
        String sender = jsonObject.has("sender") ? jsonObject.get("sender").getAsString() : null;
        List<String> recipients = new ArrayList<>();
        if (jsonObject.has("recipients")) {
            JsonArray recipientsArray = jsonObject.getAsJsonArray("recipients");
            for (JsonElement recipientElement : recipientsArray) {
                recipients.add(recipientElement.getAsString());
            }
        }
        String subject = jsonObject.has("subject") ? jsonObject.get("subject").getAsString() : null;
        String body = jsonObject.has("body") ? jsonObject.get("body").getAsString() : null;
        String date = jsonObject.get("date").getAsString();

        return new Email(id, sender, recipients, subject, body, LocalDateTime.parse(date));
    }

    @Override
    public JsonElement serialize(Email src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("sender", src.getSender());
        JsonArray recipientsArray = new JsonArray();
        for (String recipient : src.getRecipients()) {
            recipientsArray.add(recipient);
        }
        jsonObject.add("recipients", recipientsArray);
        jsonObject.addProperty("subject", src.getSubject());
        jsonObject.addProperty("body", src.getBody());
        jsonObject.addProperty("date", src.getDate().toString());

        return jsonObject;
    }
        

}
