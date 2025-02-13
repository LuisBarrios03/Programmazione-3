package com.example.client1;

import com.example.client1.Models.Client;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Application extends javafx.application.Application {
    private static Client client;

    @Override
    public void start(Stage stage) {
        client = new Client();
        try {
            URL location = Application.class.getResource("login.fxml");
            if (location == null) {
                throw new IllegalStateException("Il file FXML non è stato trovato, controlla il percorso");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Scene scene = new Scene(fxmlLoader.load(), 300, 251);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (IOException e){
            e.printStackTrace();
        } catch (IllegalStateException e){
            System.err.println(e.getMessage());
        }
    }

    public static Client getClient() {
        return client;
    }

    public static void main(String[] args) {
        launch();
    }
}