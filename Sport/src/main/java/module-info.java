module org.example.sport {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires okhttp3;

    opens org.example.sport to javafx.fxml;
    exports org.example.sport;
}