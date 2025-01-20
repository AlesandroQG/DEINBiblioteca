module com.alesandro.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires net.sf.jasperreports.core;
    requires java.sql;


    opens com.alesandro.biblioteca to javafx.fxml;
    exports com.alesandro.biblioteca;
    exports com.alesandro.biblioteca.controller;
    exports com.alesandro.biblioteca.model;
    opens com.alesandro.biblioteca.controller to javafx.fxml;
    opens com.alesandro.biblioteca.model to javafx.base;
}