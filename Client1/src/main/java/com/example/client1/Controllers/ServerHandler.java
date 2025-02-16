package com.example.client1.Controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHandler {
    private final int serverPort;
    private final String serverAddress;
    private final Gson gson = new Gson();

    public ServerHandler(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    /**
     * Invia un comando al server.
     * La richiesta è un JsonObject, che viene convertito in stringa JSON.
     * Viene letto un singolo messaggio (assumendo che il client invii il JSON su una riga).
     */
    public JsonObject sendCommand(JsonObject data) throws IOException {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {

            // Serializziamo il JsonObject in una stringa
            String jsonString = gson.toJson(data);
            out.println(jsonString);  // Invia il JSON come stringa

            // Lettura della risposta dal server
            String responseLine = in.readLine();
            if (responseLine != null && !responseLine.isEmpty()) {
                return JsonParser.parseString(responseLine).getAsJsonObject();  // Converte la risposta in JsonObject
            } else {
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "ERRORE");
                errorResponse.addProperty("message", "Nessuna risposta dal server");
                return errorResponse;  // Risposta in caso di errore (nessuna risposta dal server)
            }
        }
    }

    /**
     * Tenta di connettersi al server e restituisce un JsonObject con lo stato della connessione.
     */
    public JsonObject tryConnection() {
        JsonObject response = new JsonObject();
        try (Socket socket = new Socket(serverAddress, serverPort)) {
            response.addProperty("status", "OK");
            response.addProperty("message", "Connesso al server con successo.");
        } catch (IOException e) {
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Impossibile connettersi al server: " + e.getMessage());
        }
        return response;  // Restituisce lo stato della connessione
    }
}
