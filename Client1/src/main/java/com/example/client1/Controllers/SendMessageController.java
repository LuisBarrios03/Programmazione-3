package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class SendMessageController {
    private Client client;
    private final ServerHandler serverHandler = new ServerHandler(5000, "localhost");

    @FXML
    private Button btn_indietro;

    @FXML
    private Button sendButton;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea messageBody;

    @FXML
    private TextField ccField;


    public void initialize() {
        client = Application.getClient();
        recipientField.textProperty().bindBidirectional(client.senderProperty());
        subjectField.textProperty().bindBidirectional(client.subjectProperty());
        messageBody.textProperty().bindBidirectional(client.bodyProperty());
        ccField.textProperty().bindBidirectional(client.recipientsProperty());

        setButtonAction(sendButton);
    }

    public void setButtonAction(Button button) {
        button.setOnAction(event -> {
            // Raccogli i dati dai campi
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String message = messageBody.getText();
            String cc = ccField.getText();

            // Crea un thread per gestire l'invio asincrono
            Thread thread = new Thread(() -> {
                try {
                    // Crea il JSON utilizzando Gson
                    JsonObject mailData = new JsonObject();
                    mailData.addProperty("id", UUID.randomUUID().toString());
                    mailData.addProperty("sender", client.getAccount());
                    mailData.addProperty("recipient", recipient);
                    mailData.addProperty("cc", cc);
                    mailData.addProperty("subject", subject);
                    mailData.addProperty("content", message);

                    JsonObject data = new JsonObject();
                    data.addProperty("action", "SEND_EMAIL");
                    JsonObject dataContainer = new JsonObject();
                    dataContainer.add("mail", mailData);
                    data.add("data", dataContainer);

                    // Invia il comando al server
                    JsonObject response = serverHandler.sendCommand(data);

                    // Gestisci la risposta
                    Platform.runLater(() -> handleResponse(response));

                } catch (IOException e) {
                    e.printStackTrace();  // Log dell'errore
                    Platform.runLater(() -> showError("Errore nella comunicazione con il server"));
                }
            });
            thread.start();
        });
    }
    private void handleResponse(JsonObject response) {
        // Controlla lo stato della risposta
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

    public void clearAllData(){
        recipientField.clear();
        subjectField.clear();
        messageBody.clear();
        ccField.clear();
    }
}
