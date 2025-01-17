package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.model.Help;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana ayuda HTML
 */
public class AyudaHTMLController implements Initializable {
    /**
     * Parámetro WebEngine
     */
    private WebEngine webEngine;

    @FXML // fx:id="arbol"
    private TreeView<Help> arbol; // Value injected by FXMLLoader

    @FXML // fx:id="visor"
    private WebView visor; // Value injected by FXMLLoader

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
        // Cargar estructura
        TreeItem<Help> root = new TreeItem<>(new Help("Raiz", ""));
        TreeItem<Help> main = new TreeItem<>(new Help(resources.getString("help.introduction"), "index.html"));
        // Categorías del manual
        TreeItem<Help> rMain = new TreeItem<>(new Help(resources.getString("help.main"), "main.html"));
        TreeItem<Help> rAlumno = new TreeItem<>(new Help(resources.getString("help.student"), "alumno.html"));
        TreeItem<Help> rLibro = new TreeItem<>(new Help(resources.getString("help.book"), "libro.html"));
        TreeItem<Help> rPrestamo = new TreeItem<>(new Help(resources.getString("help.loan"), "prestamo.html"));
        TreeItem<Help> rHistorialPrestamo = new TreeItem<>(new Help(resources.getString("help.history"), "historialPrestamo.html"));
        TreeItem<Help> rConclusion = new TreeItem<>(new Help(resources.getString("help.conclusion"), "conclusion.html"));
        main.getChildren().addAll(rMain,rAlumno,rLibro,rPrestamo,rHistorialPrestamo,rConclusion);
        // Página web
        TreeItem<Help> web = new TreeItem<>(new Help(resources.getString("help.web"), "https://google.com/", false));
        root.getChildren().addAll(main,web);
        // Ajustar propiedades de la vista árbol
        arbol.setRoot(root);
        main.setExpanded(true);
        arbol.getSelectionModel().select(main);
        arbol.setShowRoot(false);
        webEngine = visor.getEngine();
        webEngine.load(getClass().getResource("/com/alesandro/biblioteca/help/html/index.html").toExternalForm());
        // Añadimos un evento para cambiar de html al pinchar en el árbol
        arbol.setOnMouseClicked(e -> {
            if (arbol.getSelectionModel().getSelectedItem() != null) {
                Help elemento = (Help) arbol.getSelectionModel().getSelectedItem().getValue();
                if (elemento.getHtml() != null) {
                    cargarAyuda(elemento.getHtml(), elemento.isLocal());
                }
            }
        });
    }

    /**
     * Función que carga la ayuda del item seleccionado en el árbol
     *
     * @param archivo a cargar
     * @param local local o en web
     */
    private void cargarAyuda(String archivo, boolean local) {
        if (visor != null) {
            if (local) {
                webEngine.load(getClass().getResource("/com/alesandro/biblioteca/help/html/" + archivo).toExternalForm());
            } else {
                webEngine.load(archivo);
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
