package com.example.client1;

import com.example.client1.Models.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * La classe Application è l'entry point dell'applicazione JavaFX e gestisce l'avvio dell'interfaccia utente.
 * Mostra la schermata di login e inizializza l'istanza del client.
 */
public class Application extends javafx.application.Application {
    private static Client client;  /// Istanza del client che sarà utilizzata in tutta l'applicazione

    /**
     * Metodo che viene eseguito all'avvio dell'applicazione JavaFX.
     * Carica il file FXML di login e mostra la finestra principale.
     *
     * @param stage la finestra principale dell'applicazione
     */
    @Override
    public void start(Stage stage) {
        client = new Client();  /// Inizializza il client
        try {
            URL location = Application.class.getResource("login.fxml");  /// Ottiene il percorso del file FXML
            if (location == null) {
                throw new IllegalStateException("Il file FXML non è stato trovato, controlla il percorso");  /// Gestisce il caso in cui il file FXML non venga trovato
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);  /// Crea un oggetto FXMLLoader per caricare il file FXML
            Scene scene = new Scene(fxmlLoader.load(), 300, 251);  /// Crea la scena con il layout FXML
            stage.setTitle("Login");  /// Imposta il titolo della finestra
            stage.setScene(scene);  /// Imposta la scena alla finestra
            stage.show();  /// Mostra la finestra
            stage.setOnCloseRequest(event -> {  /// Gestisce l'evento di chiusura della finestra
                Platform.exit();  /// Termina l'applicazione in modo sicuro
                System.exit(0);  /// Esce dall'applicazione
            });
        } catch (IOException e) {
            e.printStackTrace();  /// Stampa lo stack trace in caso di errore nel caricamento del file FXML
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());  /// Stampa l'errore nel caso in cui il file FXML non venga trovato
        }
    }

    /**
     * Restituisce l'istanza del client.
     *
     * @return l'oggetto client
     */
    public static Client getClient() {
        return client;
    }

    /**
     * Metodo main che avvia l'applicazione JavaFX.
     *
     * @param args gli argomenti passati da riga di comando
     */
    public static void main(String[] args) {
        launch();  /// Avvia l'applicazione JavaFX
    }
}
