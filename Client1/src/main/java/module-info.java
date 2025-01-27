module com.example.client1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.client1 to javafx.fxml;
    exports com.example.client1;
}