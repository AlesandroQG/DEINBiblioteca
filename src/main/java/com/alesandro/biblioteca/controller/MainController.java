package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoAlumno;
import com.alesandro.biblioteca.dao.DaoLibro;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.language.LanguageSwitcher;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.Libro;
import com.alesandro.biblioteca.model.Prestamo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase controladora de la vista principal de la aplicación
 */
public class MainController implements Initializable {
    @FXML // fx:id="miAniadir"
    private MenuItem miAniadir; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditar"
    private MenuItem btnEditar; // Value injected by FXMLLoader

    @FXML // fx:id="btnEliminar"
    private MenuItem btnEliminar; // Value injected by FXMLLoader

    @FXML // fx:id="cbTabla"
    private ComboBox<String> cbTabla; // Value injected by FXMLLoader

    @FXML // fx:id="cbFiltro"
    private ComboBox<String> cbFiltro; // Value injected by FXMLLoader

    @FXML // fx:id="filtroNombre"
    private TextField filtroNombre; // Value injected by FXMLLoader

    @FXML // fx:id="langEN"
    private RadioMenuItem langEN; // Value injected by FXMLLoader

    @FXML // fx:id="langES"
    private RadioMenuItem langES; // Value injected by FXMLLoader

    @FXML // fx:id="tabla"
    private TableView tabla; // Value injected by FXMLLoader

    @FXML // fx:id="tgIdioma"
    private ToggleGroup tgIdioma; // Value injected by FXMLLoader

    @FXML
    private ResourceBundle resources; // ResourceBundle injected automatically by FXML loader

    private ObservableList masterData = FXCollections.observableArrayList();
    private ObservableList filteredData = FXCollections.observableArrayList();

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resources = resources;
        // Controlar acceso a la base de datos
        try {
            new DBConnect();
        } catch (SQLException e) {
            mostrarAlerta(resources.getString("db.error"));
            Platform.exit(); // Cierra la aplicación
            return;
        }
        // Select de idioma
        if (resources.getLocale().equals(new Locale("es"))) {
            langES.setSelected(true);
        } else {
            langEN.setSelected(true);
        }
        // Idioma
        tgIdioma.selectedToggleProperty().addListener((observableValue, oldToggle, newToggle) -> {
            Locale locale;
            if (langES.isSelected()) {
                locale = new Locale("es");
            } else {
                locale = new Locale("en");
            }
            new LanguageSwitcher((Stage) tabla.getScene().getWindow()).switchLanguage(locale);
        });
        // Event Listener para ComboBox
        cbTabla.getItems().addAll(resources.getString("cb.students"),resources.getString("cb.books"),resources.getString("cb.loans"));
        cbTabla.setValue(resources.getString("cb.students"));
        cbTabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals(resources.getString("cb.students"))) {
                cargarAlumnos();
            } else if (newValue.equals(resources.getString("cb.books"))) {
                cargarLibros();
            } else {
                cargarPrestamos();
            }
        });
        // Event Listener para celdas de la tabla
        tabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            deshabilitarMenus(newValue == null);
        });
        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem(resources.getString("contextmenu.edit"));
        MenuItem borrarItem = new MenuItem(resources.getString("contextmenu.delete"));
        contextMenu.getItems().addAll(editarItem,borrarItem);
        editarItem.setOnAction(this::editar);
        borrarItem.setOnAction(this::eliminar);
        tabla.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tabla.getSelectionModel().select(row.getItem());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
        // Doble-click para editar
        tabla.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                editar(null);
            }
        });
        // Carga inicial
        cargarAlumnos();
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Ayuda HTML". Abre la guía de usuario en formato HTML
     *
     * @param event
     */
    @FXML
    void ayudaHTML(ActionEvent event) {
        try {
            Window ventana = tabla.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/AyudaHTML.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
            stage.setTitle(resources.getString("window.help"));
            stage.setResizable(false);
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            mostrarAlerta("Error abriendo ventana, por favor inténtelo de nuevo");
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Ayuda PDF". Abre la guía de usuario en formato PDF
     *
     * @param event
     */
    @FXML
    void ayudaPDF(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Añadir". Abre una ventana para añadir un nuevo item
     *
     * @param event
     */
    @FXML
    void aniadir(ActionEvent event) {
        String item = cbTabla.getSelectionModel().getSelectedItem();
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Editar". Abre una ventana para editar un item
     *
     * @param event
     */
    @FXML
    void editar(ActionEvent event) {
        String item = cbTabla.getSelectionModel().getSelectedItem();
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Eliminar". Elimina el item seleccionado
     *
     * @param event
     */
    @FXML
    void eliminar(ActionEvent event) {
        String item = cbTabla.getSelectionModel().getSelectedItem();
    }

    private void mostrarInforme(String informe) {
        DBConnect connection;
        try {
            connection = new DBConnect(); // Instanciar la conexión con la base de datos
            JasperReport report = (JasperReport) JRLoader.loadObject(getClass().getResource("/com/alesandro/biblioteca/reports/" + informe + ".jasper")); // Obtener el fichero del informe
            JasperPrint jprint = JasperFillManager.fillReport(report, null, connection.getConnection()); // Cargar el informe con las personas
            JasperViewer viewer = new JasperViewer(jprint, false); // Instanciar la vista del informe para mostrar el informe
            viewer.setVisible(true); // Mostrar el informe al usuario
        } catch (JRException e) {
            System.err.println(e.getMessage());
            mostrarAlerta("Ha ocurrido un error cargando el informe");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            mostrarAlerta("Ha ocurrido un erros cargando los países de la base de datos");
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Alumnos". Abre el informe de alumnos
     *
     * @param event
     */
    @FXML
    void informeAlumnos(ActionEvent event) {
        mostrarInforme("InformeAlumnos");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Gráficos". Abre el informe de gráficos
     *
     * @param event
     */
    @FXML
    void informeGraficos(ActionEvent event) {
        mostrarInforme("InformeGraficos");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Libros". Abre el informe de libros
     *
     * @param event
     */
    @FXML
    void informeLibros(ActionEvent event) {
        mostrarInforme("InformeLibros");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Salir". Cierra la aplicación
     *
     * @param event
     */
    @FXML
    void cerrar(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Función que deshabilita o habilita los menus de edición
     *
     * @param deshabilitado los menus
     */
    private void deshabilitarMenus(boolean deshabilitado) {
        btnEditar.setDisable(deshabilitado);
        btnEliminar.setDisable(deshabilitado);
    }

    /**
     * Función que cambia los textos de los items de edición del menú
     *
     * @param text texto a cambiar
     */
    private void editarMenuItemText(String text) {
        miAniadir.setText(resources.getString("menu.file.add") + " " + text.toLowerCase());
        btnEditar.setText(resources.getString("menu.edit.edit") + " " + text.toLowerCase() + "...");
        btnEliminar.setText(resources.getString("menu.edit.delete") + " " + text.toLowerCase() + "...");
    }

    /**
     * Función que carga en la tabla las columnas de alumnos y los alumnos
     */
    public void cargarAlumnos() {
        // Vaciar tabla
        tabla.getSelectionModel().clearSelection();
        filtroNombre.setText(null);
        filtroNombre.setDisable(false);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.student"));
        // Cargar filas
        TableColumn<Alumno, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory("dni"));
        TableColumn<Alumno, String> colNombre = new TableColumn<>(resources.getString("table.student.name"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        TableColumn<Alumno, String> colApellido1 = new TableColumn<>(resources.getString("table.student.surname1"));
        colApellido1.setCellValueFactory(new PropertyValueFactory("apellido1"));
        TableColumn<Alumno, String> colApellido2 = new TableColumn<>(resources.getString("table.student.surname2"));
        colApellido2.setCellValueFactory(new PropertyValueFactory("apellido2"));
        tabla.getColumns().addAll(colDni,colNombre,colApellido1,colApellido2);
        // Rellenar tabla
        ObservableList<Alumno> alumnos = DaoAlumno.cargarListado();
        masterData.setAll(alumnos);
        tabla.setItems(alumnos);
    }

    /**
     * Función que carga en la tabla las columnas de libros y los libros
     */
    public void cargarLibros() {
        // Vaciar tabla
        tabla.getSelectionModel().clearSelection();
        filtroNombre.setText(null);
        filtroNombre.setDisable(false);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.book"));
        // Cargar filas
        TableColumn<Libro, Integer> colCodigo = new TableColumn<>(resources.getString("table.book.code"));
        colCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        TableColumn<Libro, String> colTitulo = new TableColumn<>(resources.getString("table.book.title"));
        colTitulo.setCellValueFactory(new PropertyValueFactory("titulo"));
        TableColumn<Libro, String> colAutor = new TableColumn<>(resources.getString("table.book.author"));
        colAutor.setCellValueFactory(new PropertyValueFactory("autor"));
        TableColumn<Libro, String> colEditorial = new TableColumn<>(resources.getString("table.book.publisher"));
        colEditorial.setCellValueFactory(new PropertyValueFactory("editorial"));
        TableColumn<Libro, String> colEstado = new TableColumn<>(resources.getString("table.book.status"));
        colEstado.setCellValueFactory(new PropertyValueFactory("estado"));
        TableColumn<Libro, String> colBaja = new TableColumn<>(resources.getString("table.book.leave"));
        colBaja.setCellValueFactory(new PropertyValueFactory("baja"));
        tabla.getColumns().addAll(colCodigo,colTitulo,colAutor,colEditorial,colEstado,colBaja);
        // Rellenar tabla
        ObservableList<Libro> libros = DaoLibro.cargarListado();
        masterData.setAll(libros);
        tabla.setItems(libros);
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarPrestamos() {
        // Vaciar tabla
        tabla.getSelectionModel().clearSelection();
        filtroNombre.setText(null);
        filtroNombre.setDisable(false);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.loan"));
        // Cargar filas
        TableColumn<Prestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory("id_prestamo"));
        TableColumn<Prestamo, String> colAlumno = new TableColumn<>(resources.getString("table.loan.student"));
        colAlumno.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getAlumno().getNombre()));
        TableColumn<Prestamo, String> colLibro = new TableColumn<>(resources.getString("table.loan.book"));
        colLibro.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<Prestamo, LocalDateTime> colFechaPrestamo = new TableColumn<>(resources.getString("table.loan.loan_date"));
        colFechaPrestamo.setCellValueFactory(new PropertyValueFactory("fecha_prestamo"));
        tabla.getColumns().addAll(colId,colAlumno,colLibro,colFechaPrestamo);
        // Rellenar tabla
        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        masterData.setAll(prestamos);
        tabla.setItems(prestamos);
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Info");
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