package com.example.client1.Controllers;

import com.example.client1.Models.Client;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.example.client1.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private Client client;
    private final ServerHandler serverHandler = new ServerHandler(5000, "localhost");
    private Thread loginThread;

    @FXML
    private Label email_incorrect;

    @FXML
    private Button btn_invia;

    @FXML
    private TextField txt_email;

    @FXML
    public void init(ActionEvent event) {
        String account = txt_email.getText();
        client = Application.getClient();
        client.setAccount(account);

        if (!isValid(account)) {
            showError("Indirizzo email non valido");
        } else {
            performLogin(account);
        }
    }

    private void performLogin(String account) {
        JsonObject data = createLoginJson(account);

        loginThread = new Thread(() -> {
            try {
                JsonObject response = serverHandler.sendCommand(data);
                Platform.runLater(() -> handleLoginResponse(response));
            } catch (IOException ex) {
                Platform.runLater(() -> showError("Errore di connessione con il server"));
            }
        });
        loginThread.start();
    }

    private JsonObject createLoginJson(String account) {
        JsonObject mailData = new JsonObject();
        mailData.addProperty("sender", account);

        JsonObject data = new JsonObject();
        data.addProperty("action", "LOGIN");
        JsonObject dataContainer = new JsonObject();
        dataContainer.add("mail", mailData);
        data.add("data", dataContainer);

        return data;
    }

    private void handleLoginResponse(JsonObject response) {
        if (response.get("status").getAsString().equals("OK")) {
            loadMenu();
        } else {
            showError("Login fallito: " + response.get("message").getAsString());
        }
    }

    private void loadMenu() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/client1/menu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btn_invia.getScene().getWindow();
            stage.setTitle("Menu");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid(String account) {
        return account != null && account.matches("^[a-zA-Z0-9._%+-]+@notamail\\.com$");
    }

    private void showError(String message) {
        email_incorrect.setText(message);
        email_incorrect.setVisible(true);
    }
}
