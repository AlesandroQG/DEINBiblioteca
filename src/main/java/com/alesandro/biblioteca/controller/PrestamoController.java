package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoAlumno;
import com.alesandro.biblioteca.dao.DaoLibro;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.Libro;
import com.alesandro.biblioteca.model.Prestamo;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana préstamo
 */
public class PrestamoController implements Initializable {
    /**
     * Parámetro préstamo
     */
    private Prestamo prestamo;

    @FXML // fx:id="cbAlumno"
    private ComboBox<Alumno> cbAlumno; // Value injected by FXMLLoader

    @FXML // fx:id="cbLibro"
    private ComboBox<Libro> cbLibro; // Value injected by FXMLLoader

    @FXML // fx:id="datePicker"
    private DatePicker datePicker; // Value injected by FXMLLoader

    @FXML // fx:id="lblId"
    private Label lblId; // Value injected by FXMLLoader

    @FXML // fx:id="hora"
    private Spinner<Integer> hora; // Value injected by FXMLLoader

    @FXML // fx:id="minuto"
    private Spinner<Integer> minuto; // Value injected by FXMLLoader

    /**
     * Recursos de la aplicación (strings del multiidioma)
     */
    private ResourceBundle resources;

    /**
     * Constructor con parámetros para la consulta o edición de un préstamo
     *
     * @param prestamo a consultar o editar
     */
    public PrestamoController(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * Constructor vacío para la creación de un préstamo
     */
    public PrestamoController() {
        this.prestamo = null;
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
        // Cargar los datos a los ComboBoxes
        ObservableList<Alumno> alumnos = DaoAlumno.cargarListado();
        cbAlumno.getItems().add(new Alumno(null,resources.getString("loan.student.select"),null,null));
        cbAlumno.getItems().addAll(alumnos);
        cbAlumno.getSelectionModel().select(0);
        ObservableList<Libro> libros = DaoLibro.cargarListado();
        cbLibro.getItems().add(new Libro(0,resources.getString("loan.book.select"),null,null,null,0,null));
        cbLibro.getItems().addAll(libros);
        cbLibro.getSelectionModel().select(0);
        // Cargar préstamo a editar
        if (prestamo != null) {
            lblId.setText(prestamo.getId_prestamo() + "");
            cbAlumno.getSelectionModel().select(prestamo.getAlumno());
            cbLibro.getSelectionModel().select(prestamo.getLibro());
            datePicker.setValue(prestamo.getFecha_prestamo().toLocalDate());
            hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, prestamo.getFecha_prestamo().getHour()));
            minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, prestamo.getFecha_prestamo().getMinute()));
        } else {
            datePicker.setValue(LocalDate.now());
            hora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
            minuto.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Cancelar". Cierra la ventana
     *
     * @param event evento del usuario
     */
    @FXML
    void cancelar(ActionEvent event) {
        Stage stage = (Stage) cbAlumno.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que se ejecuta cuando se pulsa el botón "Generar Informe". Valida y guarda los datos del formulario
     *
     * @param event evento del usuario
     */
    @FXML
    void generarInforme(ActionEvent event) {
        DBConnect connection;
        HashMap<String, Object> parameters = new HashMap<>();
        try {
            connection = new DBConnect(); // Instanciar la conexión con la base de datos
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/com/alesandro/biblioteca/reports/InformeAltaPrestamo.jasper")); // Obtener el fichero del informe
            JasperPrint jprint = JasperFillManager.fillReport(report, parameters, connection.getConnection()); // Cargar el informe
            JasperViewer viewer = new JasperViewer(jprint, false); // Instanciar la vista del informe para mostrar el informe
            viewer.setVisible(true); // Mostrar el informe al usuario
        } catch (JRException e) {
            System.err.println(e.getMessage());
            mostrarAlerta(resources.getString("report.load.error"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            mostrarAlerta(resources.getString("report.load.db.error"));
        }
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
            if (prestamo == null) {
                // Nuevo
                Prestamo prestamo1 = new Prestamo();
                prestamo1.setAlumno(cbAlumno.getSelectionModel().getSelectedItem());
                prestamo1.setLibro(cbLibro.getSelectionModel().getSelectedItem());
                LocalDateTime fecha_prestamo = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
                prestamo1.setFecha_prestamo(fecha_prestamo);
                if (DaoPrestamo.insertar(prestamo1) != -1) {
                    mostrarConfirmacion(resources.getString("save.loan"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.fail"));
                }
            } else {
                // Actualizar
                Prestamo prestamo1 = new Prestamo();
                prestamo1.setId_prestamo(prestamo.getId_prestamo());
                prestamo1.setAlumno(cbAlumno.getSelectionModel().getSelectedItem());
                prestamo1.setLibro(cbLibro.getSelectionModel().getSelectedItem());
                LocalDateTime fecha_prestamo = LocalDateTime.of(datePicker.getValue().getYear(),datePicker.getValue().getMonth(),datePicker.getValue().getDayOfMonth(),hora.getValue(),minuto.getValue());
                prestamo1.setFecha_prestamo(fecha_prestamo);
                if (DaoPrestamo.modificar(prestamo1)) {
                    mostrarConfirmacion(resources.getString("update.loan"));
                    cancelar(null);
                } else {
                    mostrarAlerta(resources.getString("save.fail"));
                }
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
        if (cbAlumno.getSelectionModel().getSelectedItem().getNombre().equals(resources.getString("loan.student.select"))) {
            error += resources.getString("validate.loan.student") + "\n";
        }
        if (cbLibro.getSelectionModel().getSelectedItem().getTitulo().equals(resources.getString("loan.book.select"))) {
            error += resources.getString("validate.loan.book") + "\n";
        }
        if (datePicker.getValue() == null) {
            error += resources.getString("validate.loan.loan_date") + "\n";
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
