package com.example.client1.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class HomeController {
    @FXML
    public void StatusChanger(ActionEvent event) throws IOException {
        //TODO stato di connessione al server
    }

    @FXML
    public void newMail_btn_change (ActionEvent event) throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client1/send_message.fxml"));
            Parent root = loader.load();
            Stage newWindow = new Stage();
            newWindow.setTitle("New Mail Scene");
            newWindow.setScene(new Scene(root));
            newWindow.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
