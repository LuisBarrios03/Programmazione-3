package com.example.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerApplication.class.getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 450, 450);
        stage.setTitle("Server Application");
        stage.setScene(scene);
        stage.show();
        //zio pera git hub è una merda
    }

    public static void main(String[] args) {
        launch();
    }
}