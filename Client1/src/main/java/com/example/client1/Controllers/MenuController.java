package com.example.client1.Controllers;

import com.example.client1.Application;
import com.example.client1.Models.Email;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.client1.Models.Client;
import javafx.scene.control.cell.PropertyValueFactory;


public class MenuController {
    private Client client;
    private ServerHandler Serverhandler;


    @FXML
    private Button btn_nuovamail;

    @FXML
    private Button btn_cancellamail;

    @FXML
    private Button btn_aggiornamailbox;

    @FXML
    private Button btn_logout;

    @FXML
    private Label lbl_connessione;

    @FXML
    private TableView<Email> inbox;

    @FXML
    private TableColumn<Email, Boolean> inbox_crocette;

    @FXML
    private TableColumn<Email, String> inbox_titolo;

    @FXML
    private TextArea textemail;

    @FXML
    private Button btn_rispondi;

    @FXML
    private Button btn_inoltra;

    @FXML
    private Button btn_inoltratutti;

    @FXML
    public void init(){
        client = Application.getClient();
        lbl_connessione.textProperty().bind(client.connectionProperty());
        inbox_titolo.setCellValueFactory(new PropertyValueFactory<>("Subject"));

        inbox.setItems(client.mailListProperty());

        inbox_crocette.setCellValueFactory(cellData ->cellData.getValue().selectedProperty());
        inbox_crocette.setCellValueFactory(tc -> new TableCell<>(){
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(e -> {

                });
            }
        });
    }
}
