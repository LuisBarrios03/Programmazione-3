package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.example.client1.Models.Email;
import com.google.gson.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
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

/**
 * Controller per la gestione del menu principale dell'applicazione.
 * la gestione delle email e il logout dell'utente.
 */
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
    private Button btn_rispondiTutti;
    @FXML
    private Label lbl_error;

    /**
     * Inizializza il controller del menu.
     *
     * Questo metodo viene chiamato automaticamente dopo il caricamento del file FXML.
     */
    @FXML
    public void initialize() {
        client = Application.getClient();
        lbl_menu_title.setText("Benvenuto, " + client.getAccount());
        lbl_connessione.setText("Stato Connessione: Online");
        lbl_connessione.setVisible(true);
        inbox_titolo.setCellValueFactory(new PropertyValueFactory<>("subject"));
        inbox_crocette.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        inbox_crocette.setCellFactory(tc -> createCheckBoxTableCell());
        inbox.itemsProperty().bind(client.mailListProperty());
        inbox.getSelectionModel().selectedItemProperty().addListener((obs, oldEmail, selectedEmail) -> {
            if (selectedEmail != null) {
                String content = String.format("Mittente: %s\nDestinatari: %s\nOggetto: %s\n\nTesto: %s\nData: %s",
                        selectedEmail.getSender(),
                        selectedEmail.getRecipients(),
                        selectedEmail.getSubject(),
                        selectedEmail.getBody(),
                        selectedEmail.getDate());
                textemail.setText(content);
            }
        });

        scheduleConnectionStatusUpdates();
        updateInbox();
    }

    /**
     * Crea una cella con checkbox per la tabella delle email.
     *
     * @return Una cella con checkbox.
     */
    private CheckBoxTableCell<Email, Boolean> createCheckBoxTableCell() {
        return new CheckBoxTableCell<>() {
            final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {
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

    /**
     * Aggiorna manualmente la casella di posta.
     *
     * @param e L'evento di azione generato dal pulsante di aggiornamento.
     */
    @FXML
    public void manualRefresh(ActionEvent e) {
        updateInbox();
    }

    /**
     * Aggiorna la casella di posta inviando una richiesta al server.
     */
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

    /**
     * Gestisce la risposta del server dopo l'aggiornamento della casella di posta.
     *
     * @param response La risposta JSON ricevuta dal server.
     */
    private void handleInboxResponse(JsonObject response) {
        if (response.has("status") && !response.get("status").isJsonNull()
                && response.get("status").getAsString().equals("OK")) {
            if (!response.has("emails") || response.get("emails").isJsonNull()) {
                updateMailList(new ArrayList<>());
                showError("Nessun dato ricevuto dal server.");
                return;
            }

            JsonElement dataElement = response.get("emails");
            JsonArray mailList = null;
            if (dataElement.isJsonArray()) {
                mailList = dataElement.getAsJsonArray();
            }
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
            String errorMsg = (response.has("message") && !response.get("message").isJsonNull())
                    ? response.get("message").getAsString()
                    : "Errore sconosciuto";

            showError("Errore durante l'aggiornamento della casella di posta: " + errorMsg);
        }
    }

    /**
     * Aggiorna la lista delle email nel client.
     *
     * @param emails La nuova lista di email.
     */
    private void updateMailList(List<Email> emails) {
        Platform.runLater(() -> {
            System.out.println("Aggiornamento casella di posta");
            client.updateMailList(emails);
            System.out.println("Casella di posta aggiornata");
        });
    }

    /**
     * Mostra un messaggio di errore nell'interfaccia utente.
     *
     * @param message Il messaggio di errore da visualizzare.
     */
    private void showError(String message) {
        Platform.runLater(() -> lbl_error.setText(message));
    }

    /**
     * Pianifica aggiornamenti periodici dello stato della connessione.
     */
    public void scheduleConnectionStatusUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateConnectionStatus();
                updateInbox();
            } catch (Exception e) {
                showError("Errore di connessione con il server");
            }
        }, 1, 10, TimeUnit.SECONDS);
    }

    /**
     * Aggiorna lo stato della connessione inviando una richiesta al server.
     */
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

    /**
     * Effettua il logout dell'utente e carica la schermata di login.
     *
     * @param e L'evento di azione generato dal pulsante di logout.
     */
    public void logOut(ActionEvent e) {
        // 1. Resettare tutte le informazioni del client
        client.resetClient(); // Aggiungi un metodo nel tuo modello Client per resettare i dati

        // 2. Svuotare la lista delle e-mail
        client.getMailList().clear();  // Azzera la lista delle e-mail

        // 3. Azzerare le informazioni della connessione
        client.setConnection("OFF");
        lbl_connessione.setText("Stato Connessione: Offline");

        // 4. Reimpostare l'interfaccia utente (schermata di login)
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

    /**
     * Carica la schermata per l'invio di una nuova email.
     *
     * @param e L'evento di azione generato dal pulsante per la nuova email.
     */
    public void newEmail(ActionEvent e) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/send_message.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btn_nuovamail.getScene().getWindow();
            stage.setTitle("Invio Messaggio");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Popola la tabella delle email.
     */
    public void populateTable() {
        try {
        } catch (Exception e) {
            showError("Errore durante il popolamento della tabella");
        }
    }

    /**
     * Cancella le email selezionate.
     *
     * @param e L'evento di azione generato dal pulsante di cancellazione.
     */
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
                        return true;
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

    /**
     * Gestisce le azioni di risposta e inoltro delle email.
     *
     * @param e L'evento di azione generato dal pulsante di risposta o inoltro.
     */
    @FXML
    public void replyFowardEmail(ActionEvent e) {
        List<Email> selectedEmails = client.getMailList().stream()
                .filter(Email::isSelected).toList();
        if (selectedEmails.size() != 1) {
            showError("Seleziona una sola email per rispondere/inoltrare.");
            return;
        }
        Email selectedEmail = selectedEmails.get(0);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/send_message.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            SendMessageController controller = fxmlLoader.getController();
            if (e.getSource() == btn_rispondi) {
                controller.initReply(selectedEmail);
            } else if (e.getSource() == btn_inoltra) {
                controller.initForward(selectedEmail);
            } else if (e.getSource() == btn_rispondiTutti){
                controller.initReplyAll(selectedEmail);
            }

            Stage stage = (Stage) ((Button) e.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showError("Errore durante la gestione della richiesta.");
        }
    }

    /**
     * Seleziona tutte le email nella casella di posta.
     *
     * @param e L'evento di azione generato dal pulsante di selezione.
     */
    public void selectAll(ActionEvent e){
        client.getMailList().forEach(email -> email.setSelected(true));
    }
}