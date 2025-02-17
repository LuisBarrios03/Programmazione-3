package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// Classe principale dell'applicazione server
public class ServerApplication extends Application {
    // Variabile per controllare se l'interfaccia è già stata aperta
    boolean guiAlreadyOpened;

    // Metodo principale per avviare l'interfaccia
    @Override
    public void start(Stage stage) throws IOException {
        // Evita di aprire più finestre dell'interfaccia
        if (guiAlreadyOpened) {
            return;
        }
        guiAlreadyOpened = true;

        // Caricamento dell'interfaccia grafica da file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.example.server/server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);

        // Imposta il titolo e la scena nella finestra principale
        stage.setTitle("Server Application");
        stage.setScene(scene);

        // Comportamento alla chiusura della finestra
        stage.setOnCloseRequest(event -> {
            System.exit(0); // Arresta l'applicazione al click su 'X'
        });

        // Mostra l'interfaccia
        stage.show();
    }

    // Metodo principale per eseguire l'applicazione
    public static void main(String[] args) {
        launch(); // Avvia l'applicazione JavaFX
    }
}
