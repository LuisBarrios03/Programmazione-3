package com.example.server.controller;

import com.example.server.model.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;

/**
 * Controller per la gestione del server.
 * @details Questa classe gestisce le azioni dell'utente relative all'avvio e alla fermata del server,
 * nonché la registrazione dei log nel GUI del server.
 */
public class ServerController {
    private Server server;               /// Oggetto Server per la gestione delle operazioni del server
    private boolean running = false;      /// Stato di esecuzione del server (avviato o fermo)

    @FXML
    private Label serverStatusLabel;      /// Etichetta per mostrare lo stato del server
    @FXML
    private Button startServerButton;     /// Bottone per avviare il server
    @FXML
    private Button stopServerButton;      /// Bottone per fermare il server
    @FXML
    private TextArea logArea;             /// Area di testo per visualizzare i log

    /**
     * Avvia il server.
     * @param e l'evento di azione
     * @details Questo metodo viene invocato quando l'utente preme il bottone per avviare il server.
     * Il server viene avviato in un nuovo thread e i relativi controlli UI vengono aggiornati.
     */
    @FXML
    public void startServer(ActionEvent e) {
        if (!running) {
            server = new Server(5000, new File("data"), this, 3);    /// Crea un nuovo server su porta 5000 con i parametri specificati
            server.startReceiving();                                   /// Avvia il processo di ricezione
            running = true;                                            /// Imposta lo stato del server su "avviato"

            serverStatusLabel.setText("Server Online");                /// Aggiorna l'etichetta con lo stato del server
            serverStatusLabel.setStyle("-fx-text-fill: green");        /// Cambia il colore della scritta in verde
            startServerButton.setDisable(true);                         /// Disabilita il bottone per avviare il server
            stopServerButton.setDisable(false);                         /// Abilita il bottone per fermare il server
        }
    }

    /**
     * Ferma il server.
     * @details Questo metodo viene invocato quando l'utente preme il bottone per fermare il server.
     * Ferma il server e aggiorna i controlli UI per riflettere il cambiamento di stato.
     */
    @FXML
    public void stopServer() {
        if (running && server != null) {
            server.stopReceiving();   /// Ferma il processo di ricezione
            running = false;           /// Imposta lo stato del server su "fermato"

            serverStatusLabel.setText("Server Offline");  /// Aggiorna l'etichetta con lo stato del server
            serverStatusLabel.setStyle("-fx-text-fill: red;"); /// Cambia il colore della scritta in rosso
            startServerButton.setDisable(false);           /// Abilita il bottone per avviare il server
            stopServerButton.setDisable(true);             /// Disabilita il bottone per fermare il server
        }
    }

    /**
     * Aggiunge un messaggio al log.
     * @param message il messaggio da aggiungere
     * @details Questo metodo permette di aggiungere nuovi messaggi all'area di log della UI in modo thread-safe.
     * Viene utilizzato Platform.runLater per eseguire l'aggiornamento dell'interfaccia utente nel thread giusto.
     */
    public void appendLog(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");   /// Aggiunge il messaggio alla fine dell'area di testo del log
        });
    }
}
