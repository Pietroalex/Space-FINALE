module com.example.spacefinale {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.spacefinale to javafx.fxml;
    exports com.example.spacefinale;
}