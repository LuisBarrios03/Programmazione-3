module com.example.client1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;


    opens com.example.client1 to javafx.fxml;
    exports com.example.client1;
    exports com.example.client1.Controllers;
    opens com.example.client1.Controllers to javafx.fxml;
}