package com.example.server.controller;

import com.example.server.model.MailStorage;
import com.example.server.model.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.example.server.model.Email;
import com.example.server.model.MailBox;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableColumn<Email, String> idColumn;

    public ServerController() {
        /*mailboxes.put("alessio@notamail.com", new MailBox("alessio@notamail.com"));
        mailboxes.put("luis@notamail.com", new MailBox("luis@notamail.com"));
        mailboxes.put("gigi@notamail.com", new MailBox("gigi@notamail.com"));*/
    }
    @FXML
    public void init(){
        messagesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void startServer(ActionEvent e) {

        messagesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        if (!serverRunning) {
            serverRunning = true;
            Server.startServer(5000);
            serverStatusLabel.setText("Running");
            serverStatusLabel.setStyle("-fx-text-fill: green;");
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            ObservableList<Email> observableEmails = FXCollections.observableArrayList(mb.getAllMails());
            messagesTable.setItems(observableEmails);
            messagesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
            messagesTable.getItems().clear();
        } else {
            Server.stopServer();
        }
    }
}
