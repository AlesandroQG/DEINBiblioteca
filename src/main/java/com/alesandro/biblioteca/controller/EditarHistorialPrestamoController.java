package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoHistorialPrestamo;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana de edición de historial préstamo
 */
public class EditarHistorialPrestamoController implements Initializable {
    /**
     * Parámetro historial préstamo
     */
    private HistorialPrestamo historialPrestamo;

    @FXML // fx:id="datePickerHistorial"
    private DatePicker datePickerHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="datePickerPrestamo"
    private DatePicker datePickerPrestamo; // Value injected by FXMLLoader

    @FXML // fx:id="horaHistorial"
    private Spinner<Integer> horaHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="horaPrestamo"
    private Spinner<Integer> horaPrestamo; // Value injected by FXMLLoader

    @FXML // fx:id="lblAlumno"
    private Label lblAlumno; // Value injected by FXMLLoader

    @FXML // fx:id="lblId"
    private Label lblId; // Value injected by FXMLLoader

    @FXML // fx:id="lblLibro"
    private Label lblLibro; // Value injected by FXMLLoader

    @FXML // fx:id="minutoHistorial"
    private Spinner<Integer> minutoHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="minutoPrestamo"
    private Spinner<Integer> minutoPrestamo; // Value injected by FXMLLoader

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un historial préstamo
     *
     * @param historialPrestamo a consultar o editar
     */
    public EditarHistorialPrestamoController(HistorialPrestamo historialPrestamo) {
        this.historialPrestamo = historialPrestamo;
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
        if (historialPrestamo == null) {
            mostrarAlerta(resources.getString("history.error"));
            cancelar(null);
        } else {
            // Cargar los datos
            lblId.setText(historialPrestamo.getId_prestamo() + "");
            lblAlumno.setText(historialPrestamo.getAlumno().toString());
            lblLibro.setText(historialPrestamo.getLibro().toString());
            datePickerPrestamo.setValue(historialPrestamo.getFecha_prestamo().toLocalDate());
            horaPrestamo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, historialPrestamo.getFecha_prestamo().getHour()));
            minutoPrestamo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, historialPrestamo.getFecha_prestamo().getMinute()));
            datePickerHistorial.setValue(historialPrestamo.getFecha_devolucion().toLocalDate());
            horaHistorial.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, historialPrestamo.getFecha_devolucion().getHour()));
            minutoHistorial.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, historialPrestamo.getFecha_devolucion().getMinute()));
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) lblId.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            HistorialPrestamo historialPrestamo1 = new HistorialPrestamo();
            historialPrestamo1.setId_prestamo(historialPrestamo.getId_prestamo());
            historialPrestamo1.setAlumno(historialPrestamo.getAlumno());
            historialPrestamo1.setLibro(historialPrestamo.getLibro());
            LocalDateTime fecha_prestamo = LocalDateTime.of(datePickerPrestamo.getValue().getYear(),datePickerPrestamo.getValue().getMonth(),datePickerPrestamo.getValue().getDayOfMonth(),horaPrestamo.getValue(),minutoPrestamo.getValue());
            historialPrestamo1.setFecha_devolucion(fecha_prestamo);
            LocalDateTime fecha_devolucion = LocalDateTime.of(datePickerHistorial.getValue().getYear(),datePickerHistorial.getValue().getMonth(),datePickerHistorial.getValue().getDayOfMonth(),horaHistorial.getValue(),minutoHistorial.getValue());
            historialPrestamo1.setFecha_devolucion(fecha_devolucion);
            if (DaoHistorialPrestamo.modificar(historialPrestamo1)) {
                mostrarConfirmacion(resources.getString("update.history"));
                cancelar(null);
            } else {
                mostrarAlerta(resources.getString("save.fail"));
            }
        }
    }

    /**
     * Función que valida el formulario y devuelve un string con posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (datePickerPrestamo.getValue() == null) {
            error += resources.getString("validate.loan.loan_date") + "\n";
        }
        if (datePickerHistorial.getValue() == null) {
            error += resources.getString("validate.history.return_date") + "\n";
        }
        return error;
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
