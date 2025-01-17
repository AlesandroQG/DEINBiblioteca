package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoAlumno;
import com.alesandro.biblioteca.dao.DaoHistorialPrestamo;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import com.alesandro.biblioteca.model.Prestamo;
import com.alesandro.biblioteca.utils.FechaFormatter;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
            txtDni.setText(alumno.getDni());
            txtDni.setDisable(true);
            txtNombre.setText(alumno.getNombre());
            txtApellido1.setText(alumno.getApellido1());
            txtApellido2.setText(alumno.getApellido2());
        }
        // Event Listener para celdas de la tabla
        tablaPrestamos.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            deshabilitarBotonesPrestamos(newValue == null);
        });
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            deshabilitarBotonesHistorial(newValue == null);
        });
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
        try {
            Window ventana = tablaHistorial.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Prestamo.fxml"), resources);
            PrestamoController controlador = new PrestamoController();
            fxmlLoader.setController(controlador);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
            stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.book") + " - " + resources.getString("app.name"));
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarPrestamos();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            mostrarAlerta(resources.getString("message.window_open"));
        }
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
        HistorialPrestamo historialPrestamo = tablaHistorial.getSelectionModel().getSelectedItem();
        if (historialPrestamo != null) {
            try {
                Window ventana = tablaHistorial.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/EditarNuevoHistorialPrestamo.fxml"), resources);
                EditarHistorialPrestamoController controlador = new EditarHistorialPrestamoController(historialPrestamo);
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.book") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarHistorialPrestamos();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Editar" de la pestaña Préstamos. Abre un menú para editar el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void editarPrestamo(ActionEvent event) {
        Prestamo prestamo = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (prestamo != null) {
            try {
                Window ventana = tablaHistorial.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Prestamo.fxml"), resources);
                PrestamoController controlador = new PrestamoController(prestamo);
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setResizable(false);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.book") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarPrestamos();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar" de la pestaña Historial de Préstamos. Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarHistorial(ActionEvent event) {
        HistorialPrestamo historialPrestamo = tablaHistorial.getSelectionModel().getSelectedItem();
        if (historialPrestamo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(tablaHistorial.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle(resources.getString("window.confirm"));
            alert.setContentText(resources.getString("delete.history.prompt"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (DaoHistorialPrestamo.eliminar(historialPrestamo)) {
                    cargarHistorialPrestamos();
                    mostrarConfirmacion(resources.getString("delete.history.success"));
                } else {
                    mostrarAlerta(resources.getString("delete.history.fail"));
                }
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Eliminar" de la pestaña Préstamos. Elimina el préstamo seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminarPrestamo(ActionEvent event) {
        Prestamo prestamo = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (prestamo != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(tablaPrestamos.getScene().getWindow());
            alert.setHeaderText(null);
            alert.setTitle(resources.getString("window.confirm"));
            alert.setContentText(resources.getString("delete.loan.prompt"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (DaoPrestamo.eliminar(prestamo)) {
                    cargarPrestamos();
                    mostrarConfirmacion(resources.getString("delete.loan.success"));
                } else {
                    mostrarAlerta(resources.getString("delete.loan.fail"));
                }
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Guardar". Valída y guarda el alumno
     *
     * @param event evento del usuario
     */
    @FXML
    void guardar(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            mostrarAlerta(error);
        } else {
            if (alumno == null) {
                // Nuevo
                Alumno alumno1 = new Alumno();
                alumno1.setDni(txtDni.getText());
                alumno1.setNombre(txtNombre.getText());
                alumno1.setApellido1(txtApellido1.getText());
                alumno1.setApellido2(txtApellido2.getText());
                if (DaoAlumno.getAlumno(txtDni.getText()) == null) {
                    if (DaoAlumno.insertar(alumno1)) {
                        mostrarConfirmacion(resources.getString("save.student"));
                        cancelar(null);
                    } else {
                        mostrarAlerta(resources.getString("save.fail"));
                    }
                } else {
                    mostrarAlerta(resources.getString("save.student.fail"));
                }
            } else {
                // Actualizar
                Alumno alumno1 = new Alumno();
                alumno1.setDni(alumno.getDni());
                alumno1.setNombre(txtNombre.getText());
                alumno1.setApellido1(txtApellido1.getText());
                alumno1.setApellido2(txtApellido2.getText());
                if (DaoAlumno.modificar(alumno1)) {
                    mostrarConfirmacion(resources.getString("update.student"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.fail"));
                }
            }
        }
    }

    /**
     * Función que valida los datos del formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (txtDni.getText().isEmpty()) {
            error += resources.getString("validate.student.dni") + "\n";
        }
        if (txtNombre.getText().isEmpty()) {
            error += resources.getString("validate.student.name") + "\n";
        }
        if (txtApellido1.getText().isEmpty()) {
            error += resources.getString("validate.student.surname1") + "\n";
        }
        if (txtApellido2.getText().isEmpty()) {
            error += resources.getString("validate.student.surname2") + "\n";
        }
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
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_prestamo())));
        tablaPrestamos.getColumns().addAll(colId,colLibro,colFechaPrestamo);
        // Rellenar tabla
        if (alumno != null) {
            ObservableList<Prestamo> prestamos = DaoPrestamo.prestamosDeAlumno(alumno);
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
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_prestamo())));
        TableColumn<HistorialPrestamo, String> colFechaDevolucion = new TableColumn<>(resources.getString("table.history.return_date"));
        colFechaDevolucion.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_devolucion())));
        tablaHistorial.getColumns().addAll(colId,colLibro,colFechaPrestamo,colFechaDevolucion);
        // Rellenar tabla
        if (alumno != null) {
            ObservableList<HistorialPrestamo> historialPrestamos = DaoHistorialPrestamo.historialDeAlumno(alumno);
            tablaHistorial.setItems(historialPrestamos);
        }
    }

    /**
     * Función que deshabilita o habilita los botones de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarBotonesPrestamos(boolean deshabilitado) {
        btnEditarPrestamo.setDisable(deshabilitado);
        btnEliminarPrestamo.setDisable(deshabilitado);
    }

    /**
     * Función que deshabilita o habilita los botones de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarBotonesHistorial(boolean deshabilitado) {
        btnEditarHistorial.setDisable(deshabilitado);
        btnEliminarHistorial.setDisable(deshabilitado);
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
