package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.example.client1.Models.Email;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.client1.Controllers.LoginController.isValid;

public class SendMessageController {
    private Client client;
    private final ServerHandler serverHandler = new ServerHandler(5000, "localhost");  /// Gestisce la comunicazione con il server

    @FXML
    private Button btn_indietro;  /// Bottone per tornare indietro

    @FXML
    private Button sendButton;  /// Bottone per inviare l'email

    @FXML
    private TextField subjectField;  /// Campo per l'oggetto dell'email

    @FXML
    private TextArea messageBody;  /// Campo per il corpo del messaggio

    @FXML
    private TextField recipientField;  /// Campo per i destinatari dell'email

    /**
     * Inizializza la vista e associa i campi con le proprietà del client.
     */
    public void initialize() {
        client = Application.getClient();  /// Recupera l'oggetto client
        subjectField.textProperty().bindBidirectional(client.subjectProperty());  /// Associa l'oggetto all'email
        messageBody.textProperty().bindBidirectional(client.bodyProperty());  /// Associa il corpo dell'email
        recipientField.textProperty().bindBidirectional(client.recipientsProperty(), new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> recipients) {
                return recipients == null ? "" : String.join(",", recipients);  /// Converte la lista di destinatari in una stringa separata da virgole
            }

            @Override
            public ObservableList<String> fromString(String string) {
                return FXCollections.observableArrayList(Arrays.stream(string.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())  /// Filtra gli spazi vuoti e le stringhe vuote
                        .collect(Collectors.toList()));
            }
        });

        setButtonAction(sendButton);  /// Imposta l'azione per il bottone di invio
    }

    /**
     * Imposta l'azione del bottone di invio dell'email.
     */
    public void setButtonAction(Button button) {
        button.setOnAction(event -> {
            String subject = subjectField.getText();  /// Ottiene l'oggetto dell'email
            String message = messageBody.getText();  /// Ottiene il corpo del messaggio
            List<String> recipients = Arrays.stream(recipientField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())  /// Filtra i destinatari vuoti
                    .collect(Collectors.toList());

            if (recipients.isEmpty()) {
                showError("Devi specificare almeno un destinatario.");  /// Mostra errore se non ci sono destinatari
                return;
            }
            if (subject.isEmpty()) {
                showError("Devi specificare un oggetto.");  /// Mostra errore se non c'è l'oggetto
                return;
            }

            Thread thread = new Thread(() -> {
                try {
                    // Crea il JSON utilizzando Gson
                    JsonObject mailData = new JsonObject();
                    mailData.addProperty("id", UUID.randomUUID().toString());  /// Crea un ID univoco per l'email
                    mailData.addProperty("sender", client.getAccount());  /// Aggiunge l'indirizzo del mittente
                    JsonArray recipientsArray = new JsonArray();  /// Array per i destinatari
                    for (String recipient : recipients) {
                        if(!isValid(recipient)){
                            Platform.runLater(() -> showError("Formato delle mails non valido."));  /// Verifica se l'email è valida
                            return;
                        }
                        recipientsArray.add(recipient);  /// Aggiunge i destinatari all'array
                    }
                    mailData.add("recipients", recipientsArray);

                    mailData.addProperty("subject", subject);  /// Aggiunge l'oggetto dell'email
                    mailData.addProperty("body", message);  /// Aggiunge il corpo del messaggio
                    mailData.addProperty("date", LocalDateTime.now().toString());  /// Aggiunge la data
                    JsonObject data = new JsonObject();
                    data.addProperty("action", "SEND_EMAIL");  /// Definisce l'azione (invio email)
                    JsonObject dataContainer = new JsonObject();
                    dataContainer.add("mail", mailData);  /// Aggiunge l'email ai dati
                    data.add("data", dataContainer);
                    JsonObject response = serverHandler.sendCommand(data);  /// Invio del comando al server
                    Platform.runLater(() -> handleResponse(response));  /// Gestisce la risposta del server

                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showError("Errore nella comunicazione con il server"));  /// Mostra errore in caso di eccezione
                }
            });
            thread.start();  /// Avvia il thread per l'invio dell'email
        });
    }

    /**
     * Gestisce la risposta del server dopo l'invio dell'email.
     */
    private void handleResponse(JsonObject response) {
        if (response.get("status").getAsString().equals("OK")) {
            showSuccess("Email inviata con successo");  /// Mostra il messaggio di successo
            clearAllData();  /// Pulisce i campi
            loadmenu();  /// Ricarica il menu principale
        } else {
            showError("Errore nell'invio dell'email: " + response.get("message").getAsString());  /// Mostra errore se l'invio fallisce
        }
    }

    /**
     * Mostra un messaggio di successo.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(message);
        alert.showAndWait();  /// Mostra un'alert con il messaggio di successo
    }

    /**
     * Mostra un messaggio di errore.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.setContentText(message);
        alert.showAndWait();  /// Mostra un'alert con il messaggio di errore
    }

    /**
     * Ricarica il menu principale dopo l'invio dell'email.
     */
    public void loadmenu() {
        clearAllData();  /// Pulisce tutti i campi
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btn_indietro.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(scene);
            stage.show();  /// Ricarica la scena del menu principale
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza la vista per una risposta all'email.
     */
    public void initReply(Email email) {
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        recipientField.textProperty().unbindBidirectional(client.recipientsProperty());

        subjectField.setText("Risposta dell'Email: " + email.getSubject() + " ricevuta da: "+ email.getSender());
        subjectField.setEditable(false);  /// Impedisce di modificare l'oggetto

        recipientField.setText(email.getSender());  /// Imposta il mittente come destinatario
        recipientField.setEditable(false);  /// Impedisce di modificare il destinatario
    }

    /**
     * Inizializza la vista per un inoltro dell'email.
     */
    public void initForward(Email email) {
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        messageBody.textProperty().unbindBidirectional(client.bodyProperty());
        subjectField.setText("Inoltro dell'Email: " + email.getSubject() + "      ricevuta da: " + email.getSender());
        subjectField.setEditable(false);  /// Impedisce di modificare l'oggetto
        messageBody.setText(email.getBody());  /// Imposta il corpo del messaggio
        messageBody.setEditable(false);  /// Impedisce di modificare il corpo
    }

    /**
     * Inizializza la vista per una risposta a tutti i destinatari dell'email.
     */
    public void initReplyAll(Email email) {
        String stringRecipients = email.getRecipients().toString().replace("[", "").replace("]", "");
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        recipientField.textProperty().unbindBidirectional(client.recipientsProperty());

        recipientField.setText(stringRecipients);  /// Imposta tutti i destinatari come destinatari
        recipientField.setEditable(false);  /// Impedisce di modificare i destinatari
        subjectField.setText("Inoltro dell'Email: " + email.getSubject() + "     ricevuta da: " + stringRecipients);
        subjectField.setEditable(false);  /// Impedisce di modificare l'oggetto
    }

    /**
     * Pulisce tutti i campi di input.
     */
    public void clearAllData() {
        subjectField.clear();  /// Pulisce l'oggetto
        messageBody.clear();  /// Pulisce il corpo
        recipientField.clear();  /// Pulisce i destinatari
    }
}
