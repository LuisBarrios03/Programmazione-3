package com.example.server.controller;

import com.example.server.model.MailStorage;
import com.example.server.model.Server;
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
    private TableColumn<Email, String> senderColumn, recipientsColumn, subjectColumn, dateColumn;

    public ServerController() {
        /*mailboxes.put("alessio@notamail.com", new MailBox("alessio@notamail.com"));
        mailboxes.put("luis@notamail.com", new MailBox("luis@notamail.com"));
        mailboxes.put("gigi@notamail.com", new MailBox("gigi@notamail.com"));*/
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
            //MailStorage.createStorage();
            //MailBox mb = new MailBox("storage",null);

            for(Email email : Email.loadMailBox("storage")) {
                System.out.println(email);
            }
        }
    }

    @FXML
    public void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            Server.stopServer();

            serverStatusLabel.setText("Stopped");
            serverStatusLabel.setStyle("-fx-text-fill: red;");
            startServerButton.setDisable(false);
            stopServerButton.setDisable(true);
        } else {
            Server.stopServer();
        }
    }
}
