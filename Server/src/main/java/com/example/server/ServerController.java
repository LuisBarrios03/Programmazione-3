package com.example.server;

import com.example.server.model.Email;
import com.example.server.model.MailBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

    // Costruttore corretto
    public ServerController() {
        mailboxes.put("giorgio@mia.mail.com", new MailBox("giorgio@mia.mail.com"));
        mailboxes.put("marco@mia.mail.com", new MailBox("marco@mia.mail.com"));
        mailboxes.put("anna@mia.mail.com", new MailBox("anna@mia.mail.com"));
    }

    // Metodo per inviare un'email alle caselle postali
    public synchronized void sendEmail(Email email) {
        for (String recipient : email.getRecipients()) {
            MailBox mailbox = mailboxes.get(recipient);
            if (mailbox != null) {
                mailbox.addEmail(email);
                System.out.println("Email delivered to: " + recipient);
            } else {
                System.out.println("Error: Recipient not found - " + recipient);
            }
        }
    }

    // Metodo per avviare il server
    @FXML
    private void startServer(ActionEvent e) {
        if (!serverRunning) {
            serverRunning = true;
            Server.startServer(5000); // Avvio del server su porta 5000

            serverStatusLabel.setText("Running");
            serverStatusLabel.setStyle("-fx-text-fill: green;");
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
        }
    }

    // Metodo per fermare il server
    @FXML
    private void stopServer(ActionEvent e) {
        if (serverRunning) {
            serverRunning = false;
            Server.stopServer(); // Arresto del server

            serverStatusLabel.setText("Stopped");
            serverStatusLabel.setStyle("-fx-text-fill: red;");
            startServerButton.setDisable(false);
            stopServerButton.setDisable(true);
        }
    }
}
