package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Client;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

public class SendMessageController {
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
    @FXML
    private TextField bccField;

    private int requestType;

    private Client client;

    String from ="", to = "", cc = "", subject = "", content = "";

    private final ServerHandler serverDriver = new ServerHandler(5000, "localhost");


    public void initialize() {
        client = Application.getClient();
        recipientField.textProperty().bindBidirectional(client.senderProperty());
        subjectField.textProperty().bindBidirectional(client.subjectProperty());
        messageBody.textProperty().bindBidirectional(client.bodyProperty());
        ccField.textProperty().bindBidirectional(client.recipientsProperty());
        bccField.textProperty().bindBidirectional(client.recipientProperty());

        setButtonAction(sendButton);
    }
    public void setButtonAction(Button button) {

        button.setOnAction(event -> {
            String recipient = recipientField.getText();
            String subject = subjectField.getText();
            String message = messageBody.getText();
            String cc = ccField.getText();
            String bcc = bccField.getText();
            Thread thread = new Thread(() -> {
                try {
                    JSONObject data = new JSONObject()
                            .put("action", "SEND_EMAIL")
                            .put("data", new JSONObject()
                                    .put("mail", new JSONObject()
                                            .put("sender", client.getAccount())
                                            .put("recipient", recipient)
                                            .put("cc", cc)
                                            .put("bcc", bcc)
                                            .put("subject", subject)
                                            .put("content", message)
                                    )
                            );
                    JSONObject response = serverDriver.sendCommand(data);
                    if (response.getString("status").equals("OK")) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Successo");
                            alert.setHeaderText("Email inviata con successo");
                            alert.showAndWait();
                        });
                    } else {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Errore");
                            alert.setHeaderText("Errore nell'invio dell'email");
                            alert.setContentText(response.getString("message"));
                            alert.showAndWait();
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            clearAllData();
            loadmenu();
        });
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
        bccField.clear();
    }
}
