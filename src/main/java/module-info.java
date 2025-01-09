module com.alesandro.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires net.sf.jasperreports.core;
    requires java.sql;


    opens com.alesandro.biblioteca to javafx.fxml;
    exports com.alesandro.biblioteca;
    exports com.alesandro.biblioteca.controller;
    opens com.alesandro.biblioteca.controller to javafx.fxml;
}