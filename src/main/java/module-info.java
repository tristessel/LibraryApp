module org.example.libraryapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.libraryapp to javafx.fxml;
    exports org.example.libraryapp;
}