package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.language.LanguageSwitcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase controladora de la vista principal de la aplicación
 */
public class MainController implements Initializable {

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
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Ayuda HTML". Abre la guía de usuario en formato HTML
     *
     * @param event
     */
    @FXML
    void ayudaHTML(ActionEvent event) {
        //
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
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Editar". Abre una ventana para editar un item
     *
     * @param event
     */
    @FXML
    void editar(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Eliminar". Elimina el item seleccionado
     *
     * @param event
     */
    @FXML
    void eliminar(ActionEvent event) {
        //
    }

    private static void mostrarInforme(String informe) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Alumnos". Abre el informe de alumnos
     *
     * @param event
     */
    @FXML
    void informeAlumnos(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Gráficos". Abre el informe de gráficos
     *
     * @param event
     */
    @FXML
    void informeGraficos(ActionEvent event) {
        //
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Informe Libros". Abre el informe de libros
     *
     * @param event
     */
    @FXML
    void informeLibros(ActionEvent event) {
        //
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