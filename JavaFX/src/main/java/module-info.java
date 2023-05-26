module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;


    opens com.example.javafx to javafx.fxml;
    exports com.example.javafx;
}