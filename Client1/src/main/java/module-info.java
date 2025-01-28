module com.example.client1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.client1 to javafx.fxml;
    exports com.example.client1;
    exports com.example.client1.controllers;
    opens com.example.client1.controllers to javafx.fxml;
}