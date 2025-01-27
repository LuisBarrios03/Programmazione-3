package com.example.client1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    protected void btn_click_change(ActionEvent event) throws IOException {
        try{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/client1/home.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Home Scene");
        stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    }