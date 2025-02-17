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
/**
 * Controller per la gestione del login dell'utente.
 *
 * Questa classe gestisce l'autenticazione dell'utente tramite un'email,
 * invia i dati al server e reindirizza l'utente al menu principale in caso di successo.
 */

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


    /**
     * Metodo chiamato quando l'utente avvia il processo di login.
     *
     * @param event L'evento di azione generato dal pulsante di login.
     */
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

    /**
     * Avvia il processo di login inviando i dati al server.
     *
     * @param account L'email dell'utente.
     */
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

    /**
     * Crea un oggetto JSON con i dati di login da inviare al server.
     *
     * @param account L'email dell'utente.
     * @return Un oggetto JSON con le informazioni per il login.
     */
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

    /**
     * Gestisce la risposta del server dopo il tentativo di login.
     *
     * @param response La risposta JSON ricevuta dal server.
     */
    private void handleLoginResponse(JsonObject response) {
        if (response.get("status").getAsString().equals("OK")) {
            loadMenu();
        } else {
            showError("Login fallito: " + response.get("message").getAsString());
        }
    }

    /**
     * Carica la schermata del menu principale dopo un login riuscito.
     */
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

    /**
     * Verifica se l'email inserita è valida.
     *
     * @param account L'email da verificare.
     * @return true se l'email è valida, false altrimenti.
     */
    public static boolean isValid(String account) {
        return account != null && account.matches("^[a-zA-Z0-9._%+-]+@notamail\\.com$");
    }

    /**
     * Mostra un messaggio di errore nell'interfaccia utente.
     *
     * @param message Il messaggio di errore da visualizzare.
     */
    private void showError(String message) {
        email_incorrect.setText(message);
        email_incorrect.setVisible(true);
    }
}
