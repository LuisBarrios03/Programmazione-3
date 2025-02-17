package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.example.client1.Models.Email;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
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
    private Label lbl_menu_title;
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
    private TableColumn<Email, Boolean> inbox_crocette;
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
        lbl_menu_title.setText("Benvenuto, " + client.getAccount());
        lbl_connessione.setText("Stato Connessione: Online");
        lbl_connessione.setVisible(true);

        // Imposta il binding corretto: "subject" corrisponde a getSubject() in Email
        inbox_titolo.setCellValueFactory(new PropertyValueFactory<>("subject"));
        // La colonna per la selezione ora utilizza Email e il suo selectedProperty
        inbox_crocette.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        inbox_crocette.setCellFactory(tc -> createCheckBoxTableCell());

        inbox.itemsProperty().bind(client.mailListProperty());

        scheduleConnectionStatusUpdates();
        updateInbox();
    }

    private CheckBoxTableCell<Email, Boolean> createCheckBoxTableCell() {
        return new CheckBoxTableCell<>() {
            final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
                    // Recupera l'oggetto Email corrispondente alla riga corrente
                    Email email = getTableView().getItems().get(getIndex());
                    email.setSelected(checkBox.isSelected());
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

        String date = null;
        ListProperty<Email> emails = client.mailListProperty();
        if(!emails.isEmpty()){
            emails.sort(null);
            date = emails.get(0).getDate().toString();
        }

        mailData.addProperty("sender", client.getAccount());
        mailData.addProperty("lastChecked", date);
        data.addProperty("action", "GET_MAILBOX");
        data.add("data", mailData);

        Thread updateThread = new Thread(() -> {
            try {
                JsonObject response = serverHandler.sendCommand(data);
                handleInboxResponse(response);
                populateTable();
            } catch (IOException ex) {
                showError("Errore di connessione con il server");
            }
        });
        updateThread.start();
    }

    private void handleInboxResponse(JsonObject response) {
        if (response.has("status") && !response.get("status").isJsonNull()
                && response.get("status").getAsString().equals("OK")) {

            // Verifica se "data" esiste ed è valido
            if (!response.has("emails") || response.get("emails").isJsonNull()) {
                updateMailList(new ArrayList<>());
                showError("Nessun dato ricevuto dal server.");
                return;
            }

            JsonElement dataElement = response.get("emails");
            JsonArray mailList = null;

            // Se "data" è un array
            if (dataElement.isJsonArray()) {
                mailList = dataElement.getAsJsonArray();
            }
            // Se "data" è una stringa JSON, prova a convertirla
            else if (dataElement.isJsonPrimitive() && dataElement.getAsJsonPrimitive().isString()) {
                String jsonString = dataElement.getAsString();
                try {
                    mailList = JsonParser.parseString(jsonString).getAsJsonArray();
                } catch (JsonSyntaxException e) {
                    showError("Formato dei dati non valido.");
                    return;
                }
            }

            if (mailList != null && mailList.size() > 0) {
                Gson gsonCustom = new GsonBuilder()
                        .registerTypeAdapter(Email.class, new EmailAdapter())
                        .create();

                List<Email> emails = new ArrayList<>();
                for (JsonElement mailElement : mailList) {
                    if (mailElement.isJsonObject()) {
                        Email email = gsonCustom.fromJson(mailElement, Email.class);
                        emails.add(email);
                    }
                }
                updateMailList(emails);
            } else {
                updateMailList(new ArrayList<>());
                showError("La casella di posta è vuota.");
            }

        } else {
            // Controllo sicuro per il messaggio di errore
            String errorMsg = (response.has("message") && !response.get("message").isJsonNull())
                    ? response.get("message").getAsString()
                    : "Errore sconosciuto";

            showError("Errore durante l'aggiornamento della casella di posta: " + errorMsg);
        }
    }


    private void updateMailList(List<Email> emails) {
        Platform.runLater(() -> {
            System.out.println("Aggiornamento casella di posta");
            client.updateMailList(emails);
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
                updateInbox();
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

    public void logOut(ActionEvent e) {
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

    public void populateTable() {
        try {
            //inbox.setItems(client.mailListProperty());
        } catch (Exception e) {
            showError("Errore durante il popolamento della tabella");
        }
    }

    @FXML
    public void cancelSelected(ActionEvent e) {
        client.getMailList().removeIf(email -> {
            if (email.isSelected()) {
                JsonObject data = new JsonObject();
                JsonObject mailData = new JsonObject();
                mailData.addProperty("sender", client.getAccount());
                mailData.addProperty("id", email.getId());
                data.addProperty("action", "DELETE_EMAIL");
                data.add("data", mailData);

                try {
                    JsonObject response = serverHandler.sendCommand(data);
                    if ("OK".equals(response.get("status").getAsString())) {
                        return true; // Rimuove l'email se il server conferma l'eliminazione
                    } else {
                        Platform.runLater(() -> showError("Errore: " + response.get("message").getAsString()));
                    }
                } catch (IOException ex) {
                    Platform.runLater(() -> showError("Errore di connessione con il server"));
                }
            }
            return false;
        });
        inbox.refresh();
    }

}
