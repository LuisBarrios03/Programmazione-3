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
    private final ServerHandler serverHandler = new ServerHandler(5000, "localhost");

    @FXML
    private Button btn_indietro;

    @FXML
    private Button sendButton;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageBody;

    @FXML
    private TextField recipientField;

    public void initialize() {
        client = Application.getClient();
        subjectField.textProperty().bindBidirectional(client.subjectProperty());
        messageBody.textProperty().bindBidirectional(client.bodyProperty());
        recipientField.textProperty().bindBidirectional(client.recipientsProperty(), new StringConverter<ObservableList<String>>() {
            @Override
            public String toString(ObservableList<String> recipients) {
                return recipients == null ? "" : String.join(",", recipients);
            }

            @Override
            public ObservableList<String> fromString(String string) {
                return FXCollections.observableArrayList(Arrays.stream(string.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList()));
            }
        });

        setButtonAction(sendButton);
    }


    public void setButtonAction(Button button) {
        button.setOnAction(event -> {
            String subject = subjectField.getText();
            String message = messageBody.getText();
            List<String> recipients = Arrays.stream(recipientField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (recipients.isEmpty()) {
                showError("Devi specificare almeno un destinatario.");
                return;
            }
            if (subject.isEmpty()) {
                showError("Devi specificare un oggetto.");
                return;

            }
            Thread thread = new Thread(() -> {
                try {
                    // Crea il JSON utilizzando Gson
                    JsonObject mailData = new JsonObject();
                    mailData.addProperty("id", UUID.randomUUID().toString());
                    mailData.addProperty("sender", client.getAccount());
                    JsonArray recipientsArray = new JsonArray();
                    for (String recipient : recipients) {
                        if(!isValid(recipient)){
                            Platform.runLater(() -> showError("Formato delle mails non valido."));
                            return;
                        }
                        recipientsArray.add(recipient);
                    }
                    mailData.add("recipients", recipientsArray);

                    mailData.addProperty("subject", subject);
                    mailData.addProperty("body", message);
                    mailData.addProperty("date", LocalDateTime.now().toString());
                    JsonObject data = new JsonObject();
                    data.addProperty("action", "SEND_EMAIL");
                    JsonObject dataContainer = new JsonObject();
                    dataContainer.add("mail", mailData);
                    data.add("data", dataContainer);
                    JsonObject response = serverHandler.sendCommand(data);
                    Platform.runLater(() -> handleResponse(response));

                } catch (IOException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showError("Errore nella comunicazione con il server"));
                }
            });
            thread.start();
        });
    }

    private void handleResponse(JsonObject response) {
        if (response.get("status").getAsString().equals("OK")) {
            showSuccess("Email inviata con successo");
            clearAllData();
            loadmenu();
        } else {
            showError("Errore nell'invio dell'email: " + response.get("message").getAsString());
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Si è verificato un errore");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadmenu() {
        clearAllData();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btn_indietro.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initReply(Email email) {
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        recipientField.textProperty().unbindBidirectional(client.recipientsProperty());

        subjectField.setText("Risposta dell'Email: " + email.getSubject() + " ricevuta da: "+ email.getSender());
        subjectField.setEditable(false);

        recipientField.setText(email.getSender());
        recipientField.setEditable(false);
    }

    public void initForward(Email email) {
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        messageBody.textProperty().unbindBidirectional(client.bodyProperty());
        subjectField.setText("Inoltro dell'Email: " + email.getSubject() + "      ricevuta da: " + email.getSender());
        subjectField.setEditable(false);
        messageBody.setText(email.getBody());
        messageBody.setEditable(false);
    }

    public void initReplyAll(Email email) {
        String stringRecipients= email.getRecipients().toString().replace("[", "").replace("]", "");
        subjectField.textProperty().unbindBidirectional(client.subjectProperty());
        recipientField.textProperty().unbindBidirectional(client.recipientsProperty());

        recipientField.setText(stringRecipients);
        recipientField.setEditable(false);
        subjectField.setText("Inoltro dell'Email: " + email.getSubject() + "     ricevuta da: " + stringRecipients);
        subjectField.setEditable(false);

    }

    public void clearAllData() {
        subjectField.clear();
        messageBody.clear();
        recipientField.clear();
    }
}
