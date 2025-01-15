package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoHistorialPrestamo;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import com.alesandro.biblioteca.model.Prestamo;
import com.alesandro.biblioteca.utils.FechaFormatter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana alumno
 */
public class AlumnoController implements Initializable {
    /**
     * Parámetro alumno
     */
    private Alumno alumno;

    @FXML // fx:id="btnEditarHistorial"
    private Button btnEditarHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditarPrestamo"
    private Button btnEditarPrestamo; // Value injected by FXMLLoader

    @FXML // fx:id="tabPane"
    private TabPane tabPane; // Value injected by FXMLLoader

    @FXML // fx:id="btnEliminarHistorial"
    private Button btnEliminarHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="btnEliminarPrestamo"
    private Button btnEliminarPrestamo; // Value injected by FXMLLoader

    @FXML // fx:id="tablaHistorial"
    private TableView<HistorialPrestamo> tablaHistorial; // Value injected by FXMLLoader

    @FXML // fx:id="tablaPrestamos"
    private TableView<Prestamo> tablaPrestamos; // Value injected by FXMLLoader

    @FXML // fx:id="txtApellido1"
    private TextField txtApellido1; // Value injected by FXMLLoader

    @FXML // fx:id="txtApellido2"
    private TextField txtApellido2; // Value injected by FXMLLoader

    @FXML // fx:id="txtDni"
    private TextField txtDni; // Value injected by FXMLLoader

    @FXML // fx:id="txtNombre"
    private TextField txtNombre; // Value injected by FXMLLoader

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un alumno
     *
     * @param alumno a consultar o editar
     */
    public AlumnoController(Alumno alumno) {
        this.alumno = alumno;
    }

    /**
     * Constructor vacío para la creación de un alumno
     */
    public AlumnoController() {
        this.alumno = null;
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
        if (alumno == null) {
            // Nuevo
            tabPane.setDisable(true);
        } else {
            // Editar
        }
        // Cargar las tablas
        cargarPrestamos();
        cargarHistorialPrestamos();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Añadir" de la pestaña Préstamos. Abre un menú para añadir un nuevo préstamo
     *
     * @param event evento del usuario
     */
    @FXML
    void aniadirPrestamo(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage)txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Editar" de la pestaña Historial de Préstamos. Abre un menú para editar el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void editarHistorial(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Editar" de la pestaña Préstamos. Abre un menú para editar el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void editarPrestamo(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar" de la pestaña Historial de Préstamos. Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarHistorial(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar" de la pestaña Préstamos. Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarPrestamo(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Valída y guarda el alumno
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        //
    }

    /**
     * Función que valida los datos del formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        return error;
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarPrestamos() {
        // Vaciar tabla
        tablaPrestamos.getSelectionModel().clearSelection();
        tablaPrestamos.getItems().clear();
        tablaPrestamos.getColumns().clear();
        // Cargar filas
        TableColumn<Prestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<Prestamo, String> colLibro = new TableColumn<>(resources.getString("table.loan.book"));
        colLibro.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<Prestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("table.loan.loan_date"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatear(cellData.getValue().getFecha_prestamo())));
        tablaPrestamos.getColumns().addAll(colId,colLibro,colFechaPrestamo);
        // Rellenar tabla
        if (alumno != null) {
        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        tablaPrestamos.setItems(prestamos);
        }
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarHistorialPrestamos() {
        // Vaciar tabla
        tablaHistorial.getSelectionModel().clearSelection();
        tablaHistorial.getItems().clear();
        tablaHistorial.getColumns().clear();
        // Cargar filas
        TableColumn<HistorialPrestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<HistorialPrestamo, String> colLibro = new TableColumn<>(resources.getString("table.loan.book"));
        colLibro.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<HistorialPrestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("table.loan.loan_date"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatear(cellData.getValue().getFecha_prestamo())));
        TableColumn<HistorialPrestamo, String> colFechaDevolucion = new TableColumn<>(resources.getString("table.history.return_date"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatear(cellData.getValue().getFecha_devolucion())));
        tablaHistorial.getColumns().addAll(colId,colLibro,colFechaPrestamo,colFechaDevolucion);
        // Rellenar tabla
        if (alumno != null) {
            ObservableList<HistorialPrestamo> historialPrestamos = DaoHistorialPrestamo.cargarListado();
            tablaHistorial.setItems(historialPrestamos);
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
