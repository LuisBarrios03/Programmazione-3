package com.example.client1.Controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.Socket;

public class ServerHandler {
    private final int serverPort;
    private final String serverAddress;
    private final Gson gson = new Gson();

    public ServerHandler(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }
    public JsonObject sendCommand(JsonObject data) throws IOException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
            String jsonString = gson.toJson(data);
            out.println(jsonString);
            String responseLine = in.readLine();
            if (responseLine != null && !responseLine.isEmpty()) {
                return JsonParser.parseString(responseLine).getAsJsonObject();
            } else {
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "ERRORE");
                errorResponse.addProperty("message", "Nessuna risposta dal server");
                return errorResponse;
            }
        }
    }
    public JsonObject tryConnection() {
        JsonObject response = new JsonObject();
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            response.addProperty("status", "OK");
            response.addProperty("message", "Connesso al server con successo.");
        } catch (IOException e) {
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Impossibile connettersi al server: " + e.getMessage());
        }
        return response;
    }
}
