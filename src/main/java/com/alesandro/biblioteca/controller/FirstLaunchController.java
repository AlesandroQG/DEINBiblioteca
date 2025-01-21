package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.language.LanguageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase controladora para la selección de un idioma
 */
public class FirstLaunchController implements Initializable {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(FirstLaunchController.class.getName());

    @FXML // fx:id="cbIdioma"
    private ComboBox<String> cbIdioma; // Value injected by FXMLLoader

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbIdioma.getItems().addAll("Español", "English");
        cbIdioma.getSelectionModel().select(0);
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Guardar". Guarda la selección y carga la ventana principal
     *
     * @param event evento del usuario
     */
    @FXML
    void confirmar(ActionEvent event) {
        String language = cbIdioma.getSelectionModel().getSelectedItem();
        Locale locale;
        if (language.equals("Español")) {
            locale = Locale.of("es");
        } else {
            locale = Locale.of("en");
        }
        LanguageManager.createFile(locale.getLanguage());
        Stage stage = (Stage) cbIdioma.getScene().getWindow();
        stage.close();
    }
}
