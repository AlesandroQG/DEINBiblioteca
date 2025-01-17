package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.dao.DaoAlumno;
import com.alesandro.biblioteca.dao.DaoHistorialPrestamo;
import com.alesandro.biblioteca.dao.DaoLibro;
import com.alesandro.biblioteca.dao.DaoPrestamo;
import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.language.LanguageSwitcher;
import com.alesandro.biblioteca.model.Alumno;
import com.alesandro.biblioteca.model.HistorialPrestamo;
import com.alesandro.biblioteca.model.Libro;
import com.alesandro.biblioteca.model.Prestamo;
import com.alesandro.biblioteca.utils.FechaFormatter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.util.Locale;
import java.util.Optional;
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

    @FXML // fx:id="txtFiltro"
    private TextField txtFiltro; // Value injected by FXMLLoader

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
            DBConnect db = new DBConnect();
            if (db.getConnection() == null) {
                mostrarAlerta(resources.getString("db.file.error"));
                Platform.exit();
                return;
            }
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
        cbTabla.getItems().addAll(resources.getString("cb.students"),resources.getString("cb.books"),resources.getString("cb.loans"),resources.getString("cb.history"));
        cbTabla.setValue(resources.getString("cb.students"));
        cbTabla.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals(resources.getString("cb.students"))) {
                cargarAlumnos();
            } else if (newValue.equals(resources.getString("cb.books"))) {
                cargarLibros();
            } else if (newValue.equals(resources.getString("cb.loans"))) {
                cargarPrestamos();
            } else {
                cargarHistorialPrestamos();
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
        // Event Listener para el filtro
        txtFiltro.setOnKeyTyped(keyEvent -> filtrar());
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
     * Función que filtra la tabla
     */
    public void filtrar() {
        String valor = txtFiltro.getText();
        if (valor != null) {
            valor = valor.toLowerCase();
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.students"))) {
                // Alumno
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Alumno alumno = (Alumno) obj;
                        String nombre = alumno.getNombre();
                        nombre = nombre.toLowerCase();
                        if (nombre.contains(valor)) {
                            filteredData.add(alumno);
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else if (item.equals(resources.getString("cb.books"))) {
                // Libro
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Libro libro = (Libro) obj;
                        String titulo = libro.getTitulo();
                        titulo = titulo.toLowerCase();
                        if (titulo.contains(valor)) {
                            filteredData.add(libro);
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else if (item.equals(resources.getString("cb.loans"))) {
                // Préstamos
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        Prestamo prestamo = (Prestamo) obj;
                        if (cbFiltro.getSelectionModel().getSelectedItem().equals("main.label.filter.student")) {
                            // Alumno
                            String nombre = prestamo.getAlumno().getNombre().toLowerCase();
                            if (nombre.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        } else if (cbFiltro.getSelectionModel().getSelectedItem().equals("main.label.filter.book_title")) {
                            // Título de libro
                            String titulo = prestamo.getLibro().getTitulo().toLowerCase();
                            if (titulo.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        } else {
                            // Autor de libro
                            String autor = prestamo.getLibro().getAutor().toLowerCase();
                            if (autor.contains(valor)) {
                                filteredData.add(prestamo);
                            }
                        }
                    }
                    tabla.setItems(filteredData);
                }
            } else {
                // Historial
                if (valor.isEmpty()) {
                    tabla.setItems(masterData);
                } else {
                    filteredData.clear();
                    for (Object obj : masterData) {
                        HistorialPrestamo historialPrestamo = (HistorialPrestamo) obj;
                        if (cbFiltro.getSelectionModel().getSelectedItem().equals("main.label.filter.student")) {
                            // Alumno
                            String nombre = historialPrestamo.getAlumno().getNombre().toLowerCase();
                            if (nombre.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        } else if (cbFiltro.getSelectionModel().getSelectedItem().equals("main.label.filter.book_title")) {
                            // Título de libro
                            String titulo = historialPrestamo.getLibro().getTitulo().toLowerCase();
                            if (titulo.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        } else {
                            // Autor de libro
                            String autor = historialPrestamo.getLibro().getAutor().toLowerCase();
                            if (autor.contains(valor)) {
                                filteredData.add(historialPrestamo);
                            }
                        }
                    }
                    tabla.setItems(filteredData);
                }
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Ayuda HTML". Abre la guía de usuario en formato HTML
     *
     * @param event evento del usuario
     */
    @FXML
    void ayudaHTML(ActionEvent event) {
        try {
            Window ventana = tabla.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/AyudaHTML.fxml"),resources);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
            stage.setTitle(resources.getString("window.help") + " - " + resources.getString("app.name"));
            stage.initOwner(ventana);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            mostrarAlerta(resources.getString("message.window_open"));
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Ayuda PDF". Abre la guía de usuario en formato PDF
     *
     * @param event evento del usuario
     */
    @FXML
    void ayudaPDF(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Añadir". Abre una ventana para añadir un nuevo item
     *
     * @param event evento del usuario
     */
    @FXML
    void aniadir(ActionEvent event) {
        String item = cbTabla.getSelectionModel().getSelectedItem();
        if (item.equals(resources.getString("cb.students"))) {
            // Alumno
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Alumno.fxml"),resources);
                AlumnoController controlador = new AlumnoController();
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(600);
                stage.setMinHeight(450);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.student") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarAlumnos();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        } else if (item.equals(resources.getString("cb.books"))) {
            // Libro
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Libro.fxml"),resources);
                LibroController controlador = new LibroController();
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(400);
                stage.setMinHeight(425);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.book") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarLibros();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        } else if (item.equals(resources.getString("cb.loans"))) {
            // Préstamo
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Prestamo.fxml"),resources);
                PrestamoController controlador = new PrestamoController();
                fxmlLoader.setController(controlador);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(350);
                stage.setMinHeight(350);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.loan") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                cargarPrestamos();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        } else {
            // Historial
            try {
                Window ventana = tabla.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/NuevoHistorialPrestamo.fxml"),resources);
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setMinWidth(500);
                stage.setMinHeight(350);
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                stage.setTitle(resources.getString("window.add") + " " + resources.getString("window.history") + " - " + resources.getString("app.name"));
                stage.initOwner(ventana);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                mostrarAlerta(resources.getString("message.window_open"));
            }
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Editar". Abre una ventana para editar un item
     *
     * @param event evento del usuario
     */
    @FXML
    void editar(ActionEvent event) {
        Object seleccion = tabla.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.students"))) {
                // Alumno
                Alumno alumno = (Alumno) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Alumno.fxml"),resources);
                    AlumnoController controlador = new AlumnoController(alumno);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(600);
                    stage.setMinHeight(450);
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                    stage.setTitle(resources.getString("window.edit") + " " + resources.getString("window.student") + " - " + resources.getString("app.name"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarAlumnos();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    mostrarAlerta(resources.getString("message.window_open"));
                }
            } else if (item.equals(resources.getString("cb.books"))) {
                // Libro
                Libro libro = (Libro) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Libro.fxml"),resources);
                    LibroController controlador = new LibroController(libro);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(400);
                    stage.setMinHeight(425);
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                    stage.setTitle(resources.getString("window.edit") + " " + resources.getString("window.book") + " - " + resources.getString("app.name"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarLibros();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    mostrarAlerta(resources.getString("message.window_open"));
                }
            } else if (item.equals(resources.getString("cb.loans"))) {
                // Préstamo
                Prestamo prestamo = (Prestamo) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Prestamo.fxml"),resources);
                    PrestamoController controlador = new PrestamoController(prestamo);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(350);
                    stage.setMinHeight(350);
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                    stage.setTitle(resources.getString("window.edit") + " " + resources.getString("window.loan") + " - " + resources.getString("app.name"));
                    stage.initOwner(ventana);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.showAndWait();
                    cargarPrestamos();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    mostrarAlerta(resources.getString("message.window_open"));
                }
            } else {
                // Historial
                HistorialPrestamo historialPrestamo = (HistorialPrestamo) seleccion;
                try {
                    Window ventana = tabla.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/EditarHistorialPrestamo.fxml"),resources);
                    EditarHistorialPrestamoController controlador = new EditarHistorialPrestamoController(historialPrestamo);
                    fxmlLoader.setController(controlador);
                    Scene scene = new Scene(fxmlLoader.load());
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setMinWidth(400);
                    stage.setMinHeight(400);
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/alesandro/biblioteca/images/Biblioteca.png")));
                    stage.setTitle(resources.getString("window.edit") + " " + resources.getString("window.history") + " - " + resources.getString("app.name"));
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
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Eliminar". Elimina el item seleccionado
     *
     * @param event evento del usuario
     */
    @FXML
    void eliminar(ActionEvent event) {
        Object seleccion = tabla.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            String item = cbTabla.getSelectionModel().getSelectedItem();
            if (item.equals(resources.getString("cb.students"))) {
                // Alumno
                Alumno alumno = (Alumno) seleccion;
                if (DaoAlumno.esEliminable(alumno)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(tabla.getScene().getWindow());
                    alert.setHeaderText(null);
                    alert.setTitle(resources.getString("window.confirm"));
                    alert.setContentText(resources.getString("delete.student.prompt"));
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        if (DaoAlumno.eliminar(alumno)) {
                            cargarAlumnos();
                            mostrarConfirmacion(resources.getString("delete.student.success"));
                        } else {
                            mostrarAlerta(resources.getString("delete.student.fail"));
                        }
                    }
                } else {
                    mostrarAlerta(resources.getString("delete.student.error"));
                }
            } else if (item.equals(resources.getString("cb.books"))) {
                // Libro
                Libro libro = (Libro) seleccion;
                if (DaoLibro.esEliminable(libro)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initOwner(tabla.getScene().getWindow());
                    alert.setHeaderText(null);
                    alert.setTitle(resources.getString("window.confirm"));
                    alert.setContentText(resources.getString("delete.book.prompt"));
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        if (DaoLibro.eliminar(libro)) {
                            cargarLibros();
                            mostrarConfirmacion(resources.getString("delete.book.success"));
                        } else {
                            mostrarAlerta(resources.getString("delete.book.fail"));
                        }
                    }
                } else {
                    mostrarAlerta(resources.getString("delete.book.error"));
                }
            } else if (item.equals(resources.getString("cb.loans"))) {
                // Préstamo
                Prestamo prestamo = (Prestamo) seleccion;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(tabla.getScene().getWindow());
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
            } else {
                // Historial
                HistorialPrestamo historialPrestamo = (HistorialPrestamo) seleccion;
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initOwner(tabla.getScene().getWindow());
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
    }

    /**
     * Función que muestra el informe de Jasper Reports pasado
     *
     * @param informe a mostrar
     */
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
            mostrarAlerta(resources.getString("report.load.error"));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            mostrarAlerta(resources.getString("report.load.db.error"));
        }
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Alumnos". Abre el informe de alumnos
     *
     * @param event evento del usuario
     */
    @FXML
    void informeAlumnos(ActionEvent event) {
        mostrarInforme("InformeAlumnos");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Gráficos". Abre el informe de gráficos
     *
     * @param event evento del usuario
     */
    @FXML
    void informeGraficos(ActionEvent event) {
        mostrarInforme("InformeGraficos");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Libros". Abre el informe de libros
     *
     * @param event evento del usuario
     */
    @FXML
    void informeLibros(ActionEvent event) {
        mostrarInforme("InformeLibros");
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Salir". Cierra la aplicación
     *
     * @param event evento del usuario
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
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("main.label.filter.name"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.student"));
        // Cargar filas
        TableColumn<Alumno, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        TableColumn<Alumno, String> colNombre = new TableColumn<>(resources.getString("table.student.name"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Alumno, String> colApellido1 = new TableColumn<>(resources.getString("table.student.surname1"));
        colApellido1.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        TableColumn<Alumno, String> colApellido2 = new TableColumn<>(resources.getString("table.student.surname2"));
        colApellido2.setCellValueFactory(new PropertyValueFactory<>("apellido2"));
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
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("main.label.filter.title"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.book"));
        // Cargar filas
        TableColumn<Libro, Integer> colCodigo = new TableColumn<>(resources.getString("table.book.code"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        TableColumn<Libro, String> colTitulo = new TableColumn<>(resources.getString("table.book.title"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        TableColumn<Libro, String> colAutor = new TableColumn<>(resources.getString("table.book.author"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        TableColumn<Libro, String> colEditorial = new TableColumn<>(resources.getString("table.book.publisher"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        TableColumn<Libro, String> colEstado = new TableColumn<>(resources.getString("table.book.status"));
        colEstado.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getEstado().equals("disponible") ? resources.getString("book.status.available") : resources.getString("book.status.loaned")));
        TableColumn<Libro, String> colBaja = new TableColumn<>(resources.getString("table.book.leave"));
        colBaja.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getBaja() == 1 ? resources.getString("book.leave.yes") : resources.getString("book.leave.no")));
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
        txtFiltro.setText(null);
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("main.label.filter.student"),resources.getString("main.label.filter.book_title"),resources.getString("main.label.filter.book_author"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.loan"));
        // Cargar filas
        TableColumn<Prestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<Prestamo, String> colAlumno = new TableColumn<>(resources.getString("table.loan.student"));
        colAlumno.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getAlumno().getNombre()));
        TableColumn<Prestamo, String> colLibroTitulo = new TableColumn<>(resources.getString("table.loan.book_title"));
        colLibroTitulo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<Prestamo, String> colLibroAutor = new TableColumn<>(resources.getString("table.loan.book_author"));
        colLibroAutor.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getAutor()));
        TableColumn<Prestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("table.loan.loan_date"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_prestamo())));
        tabla.getColumns().addAll(colId,colAlumno,colLibroTitulo,colLibroAutor,colFechaPrestamo);
        // Rellenar tabla
        ObservableList<Prestamo> prestamos = DaoPrestamo.cargarListado();
        masterData.setAll(prestamos);
        tabla.setItems(prestamos);
    }

    /**
     * Función que carga en la tabla las columnas de préstamos y los préstamos
     */
    public void cargarHistorialPrestamos() {
        // Vaciar tabla
        tabla.getSelectionModel().clearSelection();
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(resources.getString("main.label.filter.student"),resources.getString("main.label.filter.book_title"),resources.getString("main.label.filter.book_author"));
        cbFiltro.getSelectionModel().select(0);
        masterData.clear();
        filteredData.clear();
        tabla.getItems().clear();
        tabla.getColumns().clear();
        editarMenuItemText(resources.getString("string.history"));
        // Cargar filas
        TableColumn<HistorialPrestamo, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_prestamo"));
        TableColumn<HistorialPrestamo, String> colAlumno = new TableColumn<>(resources.getString("table.loan.student"));
        colAlumno.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getAlumno().getNombre()));
        TableColumn<HistorialPrestamo, String> colLibroTitulo = new TableColumn<>(resources.getString("table.loan.book_title"));
        colLibroTitulo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getTitulo()));
        TableColumn<HistorialPrestamo, String> colLibroAutor = new TableColumn<>(resources.getString("table.loan.book_author"));
        colLibroAutor.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().getLibro().getAutor()));
        TableColumn<HistorialPrestamo, String> colFechaPrestamo = new TableColumn<>(resources.getString("table.loan.loan_date"));
        colFechaPrestamo.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_prestamo())));
        TableColumn<HistorialPrestamo, String> colFechaDevolucion = new TableColumn<>(resources.getString("table.history.return_date"));
        colFechaDevolucion.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> FechaFormatter.formatearString(cellData.getValue().getFecha_devolucion())));
        tabla.getColumns().addAll(colId,colAlumno,colLibroTitulo,colLibroAutor,colFechaPrestamo,colFechaDevolucion);
        // Rellenar tabla
       ObservableList<HistorialPrestamo> historialPrestamos = DaoHistorialPrestamo.cargarListado();
       masterData.setAll(historialPrestamos);
       tabla.setItems(historialPrestamos);
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