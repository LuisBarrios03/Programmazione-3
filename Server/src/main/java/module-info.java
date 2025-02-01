module com.example.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
    exports com.example.server.model;
    opens com.example.server.model to javafx.fxml;
    exports com.example.server.controller;
    opens com.example.server.controller to javafx.fxml;
}