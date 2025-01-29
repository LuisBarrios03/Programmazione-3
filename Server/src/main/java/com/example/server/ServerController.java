package com.example.server;

import com.example.server.Email;
import com.example.server.MailBox;
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

    public void MailServerController() {
        mailboxes.put("giorgio@mia.mail.com", new MailBox("giorgio@mia.mail.com"));
        mailboxes.put("marco@mia.mail.com", new MailBox("marco@mia.mail.com"));
        mailboxes.put("anna@mia.mail.com", new MailBox("anna@mia.mail.com"));
    }

    public synchronized void sendEmail(Email email) {
        for (String recipient : email.getRecipients()) {
            MailBox mailbox = mailboxes.get(recipient);
            if (mailbox != null) {
                mailbox.receiveEmail(email);
                System.out.println("com.example.server.Email delivered to: " + recipient);
            } else {
                System.out.println("Error: Recipient not found - " + recipient);
            }
        }
    }

    @FXML
    private void startServer(ActionEvent e) {
        serverStatusLabel.setText("Running");
        serverStatusLabel.setStyle("-fx-text-fill: green;");
        startServerButton.setDisable(true);
        stopServerButton.setDisable(false);
        Server server = new Server();
        Server.ConnectionServer();
    }

    @FXML
    private void stopServer(ActionEvent e) {
        serverStatusLabel.setText("Stopped");
        serverStatusLabel.setStyle("-fx-text-fill: red;");
        startServerButton.setDisable(false);
        stopServerButton.setDisable(true);
    }
}