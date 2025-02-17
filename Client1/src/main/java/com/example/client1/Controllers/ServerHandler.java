package com.example.client1.Controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.Socket;

/**
 * Classe per gestire la comunicazione con il server.
 *
 * Questa classe si occupa di inviare comandi al server e ricevere risposte.
 */
public class ServerHandler {
    private final int serverPort;
    private final String serverAddress;
    private final Gson gson = new Gson();

    /**
     * Costruttore della classe ServerHandler.
     *
     * @param serverPort La porta del server.
     * @param serverAddress L'indirizzo del server.
     */
    public ServerHandler(int serverPort, String serverAddress) {
        this.serverPort = serverPort;
        this.serverAddress = serverAddress;
    }

    /**
     * Invia un comando al server e riceve la risposta.
     *
     * @param data Il comando da inviare al server in formato JSON.
     * @return La risposta del server in formato JSON.
     * @throws IOException Se si verifica un errore di I/O durante la comunicazione con il server.
     */
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

    /**
     * Tenta di connettersi al server e restituisce lo stato della connessione.
     *
     * @return Un oggetto JSON contenente lo stato della connessione.
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
        return response;
    }
}