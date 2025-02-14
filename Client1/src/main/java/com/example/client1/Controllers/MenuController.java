package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.example.client1.Models.Email;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import org.json.JSONArray;
import org.json.JSONObject;

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

    // Corretto: la colonna ora gestisce Email e non Client
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

        inbox_titolo.setCellValueFactory(new PropertyValueFactory<>("Subject"));

        inbox_crocette.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        inbox_crocette.setCellFactory(tc -> new CheckBoxTableCell<>() {
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
        });

        inbox.setItems(client.mailListProperty());

        scheduleConnectionStatusUpdates();
        updateInbox();
    }

    @FXML
    public void manualRefresh(ActionEvent e) {
        updateInbox();
    }

    public void updateInbox() {
        JSONObject data = new JSONObject()
                .put("action", "GET_MAILBOX")
                .put("data", new JSONObject().put("mail", new JSONObject().put("sender", client.getAccount())));

        Thread updateThread = new Thread(() -> {
            try {
                JSONObject response = serverHandler.sendCommand(data);

                // Verifica se la risposta ha un campo "status" che indica successo
                if (response.getString("status").equals("OK")) {
                    // Verifica se il campo "data" è presente e non vuoto
                    if (response.has("data") && !response.isNull("data") && response.getJSONArray("data").length() > 0) {
                        JSONArray mailList = response.getJSONArray("data");
                        List<Email> emails = new ArrayList<>();
                        for (int i = 0; i < mailList.length(); i++) {
                            JSONObject mail = mailList.getJSONObject(i);
                            emails.add(new Email(
                                    mail.getString("id"),
                                    mail.getString("sender"),
                                    mail.getJSONArray("destinatari").toList().stream().map(Object::toString).toList(),
                                    mail.getString("subject"),
                                    mail.getString("body"),
                                    mail.getString("date")
                            ));
                        }
                        // Aggiorna l'interfaccia utente con le email ricevute
                        Platform.runLater(() -> {
                            client.setMailList(FXCollections.observableArrayList(emails));
                            inbox.setItems(client.mailListProperty());
                        });
                    } else {
                        // Se "data" non è presente o è vuoto, imposta la lista vuota
                        Platform.runLater(() -> {
                            client.setMailList(FXCollections.observableArrayList());
                            inbox.setItems(client.mailListProperty());
                            lbl_error.setText("La casella di posta è vuota.");
                        });
                    }
                } else {
                    // Se la risposta ha uno status diverso da "OK", mostra un errore
                    Platform.runLater(() -> lbl_error.setText("Errore durante l'aggiornamento della casella di posta: " + response.getString("message")));
                }
            } catch (IOException ex) {
                // Gestisci errori di connessione con il server
                Platform.runLater(() -> lbl_error.setText("Errore di connessione con il server"));
            }
        });
        updateThread.start();
    }

    public void scheduleConnectionStatusUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            try {
                updateConnectionStatus();
            } catch (Exception e) {
                lbl_error.setText("Errore di connessione con il server");
                System.err.println("Errore durante l'aggiornamento delle email: " + e.getMessage());
            }
        }), 1, 5, TimeUnit.SECONDS);
    }

    public void updateConnectionStatus() {
        // Modifica: analizzo il JSONObject restituito da tryConnection()
        JSONObject connectionResponse = serverHandler.tryConnection();
        if (connectionResponse.getString("status").equals("OK")) {
            client.setConnection("ON");
            lbl_connessione.setText("Stato Connessione: Online");
        } else {
            client.setConnection("OFF");
            lbl_connessione.setText("Stato Connessione: Offline");
        }
    }
}
