package com.example.client1.Controllers;

import com.example.client1.Models.ConnectionClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    private Label lbl_status_user;

    public void changeConnectionStatus(boolean isConnected) {
            if (isConnected) {
                lbl_status_user.setText("Stato Connessione: Attiva");
                lbl_status_user.setStyle("-fx-text-fill: green");
            } else {
                lbl_status_user.setText("Stato Connessione: Disattiva");
                lbl_status_user.setStyle("-fx-text-fill: red");
            }
    }

    @FXML
    private void LogOut_btn_change(ActionEvent event) {
        try {
            ConnectionClient.stopConnection(); // 🔹 Chiude la connessione
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client1/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Scene");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
