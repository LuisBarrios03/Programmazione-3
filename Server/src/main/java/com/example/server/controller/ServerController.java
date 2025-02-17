package com.example.server.controller;

import com.example.server.model.Server;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.File;

public class ServerController {
    private Server server;
    private boolean running = false;

    @FXML
    private Label serverStatusLabel;
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;
    @FXML
    private TextArea logArea;

    @FXML
    public void startServer(ActionEvent e) {
        if (!running) {
            server = new Server(5000, new File("data"), this, 3);
            server.startReceiving();
            running = true;

            serverStatusLabel.setText("Server Online");
            serverStatusLabel.setStyle("-fx-text-fill: green");
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
        }
    }

    @FXML
    public void stopServer() {
        if (running && server != null) {
            server.stopReceiving();
            running = false;

            serverStatusLabel.setText("Server Offline");
            serverStatusLabel.setStyle("-fx-text-fill: red;");
            startServerButton.setDisable(false);
            stopServerButton.setDisable(true);
        }
    }

    public void appendLog(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
        });
    }
}
