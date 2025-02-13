package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ServerApplication extends Application {
    boolean guiAlreadyOpened;

    @Override
    public void start(Stage stage) throws IOException {
        if (guiAlreadyOpened) {
            return;
        }
        guiAlreadyOpened = true;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.example.server/server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);
        stage.setTitle("Server Application");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
            launch();
    }
}