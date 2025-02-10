package com.example.server;

import com.example.server.controller.ServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        ServerController sc = new ServerController();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com.example.server/server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);
        stage.setTitle("Server Application");
        stage.setScene(scene);
        stage.show();
        stage.setOnShowing(event -> {
            sc.init();
        });
        stage.setOnCloseRequest(event -> {
            System.out.println("Chiusura della finestra rilevata. Chiudo il socket...");
            sc.stopServer();

        });
    }

    public static void main(String[] args) {
        launch();
    }
}