package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoHistorialPrestamo;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import com.alesandro.biblioteca.model.Prestamo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana historial préstamo
 */
public class NuevoHistorialPrestamoController implements Initializable {
    @FXML // fx:id="datePicker"
    private DatePicker datePicker; // Value injected by FXMLLoader

    @FXML // fx:id="hora"
    private Spinner<Integer> hora; // Value injected by FXMLLoader

    @FXML // fx:id="lista"
    private ListView<Prestamo> lista; // Value injected by FXMLLoader

    @FXML // fx:id="minuto"
    private Spinner<Integer> minuto; // Value injected by FXMLLoader

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resources = resourceBundle;
        // Cargar los items a la lista
        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        lista.setItems(prestamos);
        // Cargar
        datePicker.setValue(LocalDate.now());
        hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = "";
        if (lista.getSelectionModel().getSelectedItem() == null) {
            error += resources.getString("validate.history.loan") + "\n";
        }
        if (datePicker.getValue() == null) {
            error += resources.getString("validate.history.return_date") + "\n";
        }
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            HistorialPrestamo historialPrestamo = new HistorialPrestamo();
            Prestamo prestamo = lista.getSelectionModel().getSelectedItem();
            historialPrestamo.setId_prestamo(prestamo.getId_prestamo());
            historialPrestamo.setAlumno(prestamo.getAlumno());
            historialPrestamo.setLibro(prestamo.getLibro());
            historialPrestamo.setFecha_prestamo(prestamo.getFecha_prestamo());
            LocalDateTime fecha_devolucion = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
            historialPrestamo.setFecha_devolucion(fecha_devolucion);
            if (DaoHistorialPrestamo.insertar(historialPrestamo)) {
                if (DaoPrestamo.eliminar(prestamo)) {
                    mostrarConfirmacion(resources.getString("save.history"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.history.fail"));
                    cancelar(null);
                }
            } else {
                mostrarAlerta(resources.getString("save.fail"));
            }
        }
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
