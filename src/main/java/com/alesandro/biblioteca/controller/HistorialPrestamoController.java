package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.model.HistorialPrestamo;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana historial préstamo
 */
public class HistorialPrestamoController implements Initializable {
    /**
     * Parámetro historial préstamo
     */
    private HistorialPrestamo historialPrestamo;

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un historial préstamo
     *
     * @param historialPrestamo a consultar o editar
     */
    public HistorialPrestamoController(HistorialPrestamo historialPrestamo) {
        this.historialPrestamo = historialPrestamo;
    }

    /**
     * Constructor vacío para la creación de un historial préstamo
     */
    public HistorialPrestamoController() {
        this.historialPrestamo = null;
    }

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        //
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle(resources.getString("window.confirm"));
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Función que muestra un mensaje de alerta al usuario
     *
     * @param mensaje de error a mostrar
     */
    public void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
