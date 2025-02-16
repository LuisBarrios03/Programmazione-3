package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.example.client1.Models.Email;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MenuController {
    private Client client;
    private final ServerHandler serverHandler = new ServerHandler(5000, "localhost");
    private ScheduledExecutorService scheduler;

    @FXML
    private Button btn_nuovamail;
    @FXML
    private Button btn_cancellamail;
    @FXML
    private Button btn_aggiornamailbox;
    @FXML
    private Button btn_logout;
    @FXML
    private Label lbl_connessione;
    @FXML
    private TableView<Email> inbox;
    @FXML
    private TableColumn<Client, Boolean> inbox_crocette;
    @FXML
    private TableColumn<Email, String> inbox_titolo;
    @FXML
    private TextArea textemail;
    @FXML
    private Button btn_rispondi;
    @FXML
    private Button btn_inoltra;
    @FXML
    private Button btn_inoltratutti;
    @FXML
    private Label lbl_error;

    @FXML
    public void initialize() {
        client = Application.getClient();
        lbl_connessione.setText("Stato Connessione: Online");
        lbl_connessione.setVisible(true);

        inbox_titolo.setCellValueFactory(new PropertyValueFactory<>("subject"));
        inbox_crocette.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        inbox_crocette.setCellFactory(tc -> createCheckBoxTableCell());

        inbox.setItems(client.mailListProperty());

        scheduleConnectionStatusUpdates();
        updateInbox();
    }

    private CheckBoxTableCell<Client, Boolean> createCheckBoxTableCell() {
        return new CheckBoxTableCell<>() {
            final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    Client client = getTableView().getItems().get(getIndex());
                    client.setSelected(checkBox.isSelected());
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setGraphic(checkBox);
                    checkBox.setSelected(item != null && item);
                } else {
                    setGraphic(null);
                }
            }
        };
    }

    @FXML
    public void manualRefresh(ActionEvent e) {
        updateInbox();
    }

    public void updateInbox() {
        JsonObject data = new JsonObject();
        JsonObject mailData = new JsonObject();

        // Usa addProperty invece di add
        mailData.addProperty("sender", client.getAccount());  // aggiungi una stringa come proprietà

        data.addProperty("action", "GET_MAILBOX"); // Usa addProperty anche per la "action"
        data.add("data", mailData);  // "data" è un JsonObject, quindi si usa add()

        Thread updateThread = new Thread(() -> {
            try {
                JsonObject response = serverHandler.sendCommand(data);
                handleInboxResponse(response);
            } catch (IOException ex) {
                showError("Errore di connessione con il server");
            }
        });
        updateThread.start();
    }


    private void handleInboxResponse(JsonObject response) {
        if (response.get("status").getAsString().equals("OK")) {
            JsonArray mailList = response.getAsJsonArray("data");
            if (mailList != null && mailList.size() > 0) {
                List<Email> emails = new ArrayList<>();
                for (int i = 0; i < mailList.size(); i++) {
                    JsonObject mail = mailList.get(i).getAsJsonObject();
                    List<String> recipients = new ArrayList<>();
                    mail.getAsJsonArray("destinatari").forEach(recipient -> recipients.add(recipient.getAsString()));

                    emails.add(new Email(
                            mail.get("id").getAsString(),
                            mail.get("sender").getAsString(),
                            recipients,
                            mail.get("subject").getAsString(),
                            mail.get("body").getAsString(),
                            mail.get("date").getAsString()
                            ));
                }
                updateMailList(emails);
            } else {
                updateMailList(new ArrayList<>());
                showError("La casella di posta è vuota.");
            }
        } else {
            showError("Errore durante l'aggiornamento della casella di posta: " + response.get("message").getAsString());
        }
    }

    private void updateMailList(List<Email> emails) {
        Platform.runLater(() -> {
            System.out.println("Aggiornamento casella di posta");
            client.setMailList(FXCollections.observableArrayList(emails));
            inbox.setItems(client.mailListProperty());
            System.out.println("Casella di posta aggiornata");
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> lbl_error.setText(message));
    }

    public void scheduleConnectionStatusUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateConnectionStatus();
            } catch (Exception e) {
                showError("Errore di connessione con il server");
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void updateConnectionStatus() {
        JsonObject connectionResponse = serverHandler.tryConnection();

        Platform.runLater(() -> {
            if (connectionResponse.get("status").getAsString().equals("OK")) {
                client.setConnection("ON");
                lbl_connessione.setText("Stato Connessione: Online");
            } else {
                client.setConnection("OFF");
                lbl_connessione.setText("Stato Connessione: Offline");
            }
        });
    }

    public void logOut(ActionEvent e){
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/login.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage) btn_logout.getScene().getWindow();
                stage.setTitle("Login");
                stage.setScene(scene);
                stage.show();
            System.out.println("Logout effettuato");
        } catch (Exception ex) {
            showError("Errore durante il logout");
        }
    }


    public void newEmail(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/send_message.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btn_nuovamail.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
