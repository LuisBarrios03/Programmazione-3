package com.example.server.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.example.server.model.Email;
import com.example.server.model.MailBox;

import java.util.HashMap;
import java.util.Map;

public class ServerController {
    private final Map<String, MailBox> mailboxes = new HashMap<>();
    private boolean serverRunning = false;

    @FXML
    private Label serverStatusLabel;
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;
    @FXML
    private TableView<Email> messagesTable;
    @FXML
    private TableColumn<Email, String> idColumn, senderColumn, recipientsColumn, subjectColumn, dateColumn;

    public ServerController() {
        mailboxes.put("giorgio@mia.mail.com", new MailBox("giorgio@mia.mail.com"));
        mailboxes.put("marco@mia.mail.com", new MailBox("marco@mia.mail.com"));
        mailboxes.put("anna@mia.mail.com", new MailBox("anna@mia.mail.com"));
    }

    @FXML
    private void startServer(ActionEvent e) {
        if (!serverRunning) {
            serverRunning = true;
            Server.startServer(5000);

            serverStatusLabel.setText("Running");
            serverStatusLabel.setStyle("-fx-text-fill: green;");
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
        }
    }

    @FXML
    private void stopServer(ActionEvent e) {
        if (serverRunning) {
            serverRunning = false;
            Server.stopServer();

            serverStatusLabel.setText("Stopped");
            serverStatusLabel.setStyle("-fx-text-fill: red;");
            startServerButton.setDisable(false);
            stopServerButton.setDisable(true);
        }
    }
}
