package com.example.client1.Controllers;

import com.example.client1.Models.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import com.example.client1.Application;
import javafx.stage.Stage;
import org.json.JSONObject;

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

    // Questo metodo viene richiamato quando il pulsante viene cliccato
    @FXML
    public void init(ActionEvent event) {
        String account = txt_email.getText();
        client = Application.getClient();
        client.setAccount(account);

        if (!isValid(account)) {
            Platform.runLater(() -> email_incorrect.setVisible(true));
        } else {
            JSONObject data = new JSONObject()
                    .put("action", "LOGIN")
                    .put("data", new JSONObject().put("mail", new JSONObject().put("sender", account)));
            loginThread = new Thread(() -> {
                try {
                    JSONObject response = serverHandler.sendCommand(data);
                    if (response.getString("status").equals("OK")) {
                        Platform.runLater(this::loadMenu);
                        //loadMenu();
                    } else {
                        Platform.runLater(() -> {
                            email_incorrect.setText("Login fallito: " + response.getString("message"));
                            email_incorrect.setVisible(true);
                        });
                    }
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        email_incorrect.setText("Errore di connessione con il server");
                        email_incorrect.setVisible(true);
                    });
                }
            });
            loginThread.start();
        }
    }

    private void loadMenu() {
        try {
            System.out.println(getClass().getResource("/com/example/client1/menu.fxml"));
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

    /*private void loadMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client1/menu.fxml"));
        Parent root = loader.load();

        Platform.runLater(() -> { // ✅ Sposta l'aggiornamento UI nel JavaFX Application Thread
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Menu");
            stage.show();
        });
    }*/

    private boolean isValid(String account) {
        return account != null && account.matches("^[a-zA-Z0-9._%+-]+@notamail\\.com$");
    }
}
