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
    private final int serverPort;  /// La porta del server
    private final String serverAddress;  /// L'indirizzo del server
    private final Gson gson = new Gson();  /// Oggetto Gson per la serializzazione/deserializzazione JSON

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
        try (Socket socket = new Socket(serverAddress, serverPort);  /// Connessione al server
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);  /// Scrittura nel socket
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {  /// Lettura dal socket
            String jsonString = gson.toJson(data);  /// Serializzazione del comando in formato JSON
            out.println(jsonString);  /// Invia il comando al server
            String responseLine = in.readLine();  /// Legge la risposta dal server
            if (responseLine != null && !responseLine.isEmpty()) {
                return JsonParser.parseString(responseLine).getAsJsonObject();  /// Restituisce la risposta del server come oggetto JSON
            } else {
                // Se non viene ricevuta risposta, crea un oggetto di errore
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("status", "ERRORE");
                errorResponse.addProperty("message", "Nessuna risposta dal server");
                return errorResponse;  /// Restituisce la risposta di errore
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
        try (Socket socket = new Socket(serverAddress, serverPort)) {  /// Tentativo di connessione al server
            response.addProperty("status", "OK");
            response.addProperty("message", "Connesso al server con successo.");  /// Risposta positiva se la connessione è riuscita
        } catch (IOException e) {
            // Se la connessione fallisce, restituisce un messaggio di errore
            response.addProperty("status", "ERRORE");
            response.addProperty("message", "Impossibile connettersi al server: " + e.getMessage());
        }
        return response;  /// Restituisce lo stato della connessione
    }
}
