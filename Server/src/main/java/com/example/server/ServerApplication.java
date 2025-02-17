package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @brief Classe principale dell'applicazione server
 */
public class ServerApplication extends Application {
    /**
     * @brief Variabile per controllare se l'interfaccia è già stata aperta
     */
    boolean guiAlreadyOpened;

    /**
     * @brief Metodo principale per avviare l'interfaccia
     * @param stage Finestra principale dell'applicazione
     * @throws IOException Se il file FXML non può essere caricato
     */
    @Override
    public void start(Stage stage) throws IOException {
        ///Evita di aprire più finestre dell'interfaccia
        if (guiAlreadyOpened) {
            return;
        }
        guiAlreadyOpened = true;

        ///Caricamento dell'interfaccia grafica da file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.example.server/server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);

        ///brief Imposta il titolo e la scena nella finestra principale
        stage.setTitle("Server Application");
        stage.setScene(scene);

        ///brief Comportamento alla chiusura della finestra
        stage.setOnCloseRequest(event -> {
            System.exit(0); ///Arresta l'applicazione al click su 'X'
        });

        ///brief Mostra l'interfaccia
        stage.show();
    }

    /**
     * @brief Metodo principale per eseguire l'applicazione
     * @param args Argomenti passati da riga di comando
     */
    public static void main(String[] args) {
        launch(); ///Avvia l'applicazione JavaFX
    }
}
