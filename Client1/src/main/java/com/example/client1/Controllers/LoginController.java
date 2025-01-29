package com.example.client1.Controllers;

import com.example.client1.Models.ConnectionClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class LoginController {

    @FXML
    public void btn_click_change(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client1/home.fxml"));
            Parent root = loader.load();

            HomeController homeController = loader.getController();
            Thread clientThread = new Thread(new ConnectionClient(homeController));
            clientThread.start();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel cambio scena: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
