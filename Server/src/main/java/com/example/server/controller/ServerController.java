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

import java.time.format.DateTimeFormatter;
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
            MailStorage.createStorage();
            MailBox mb = new MailBox("storage",null);

            senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
            recipientsColumn.setCellValueFactory(new PropertyValueFactory<>("recipients"));
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            // Per la data, convertiamo LocalDateTime in Stringa
            dateColumn.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getDate().format(String.valueOf(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                    )
            );
            // Converti la lista in ObservableList
            ObservableList<Email> observableEmails = FXCollections.observableArrayList(mb.getAllMails());
            messagesTable.setItems(observableEmails);
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
