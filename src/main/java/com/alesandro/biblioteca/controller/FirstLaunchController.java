package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.db.DBConnect;
import com.alesandro.biblioteca.language.LanguageManager;
import com.alesandro.biblioteca.utils.ValidadorNumero;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Clase controladora para la selección de un idioma
 */
public class FirstLaunchController implements Initializable {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(FirstLaunchController.class.getName());

    @FXML // fx:id="btnConfirmar"
    private Button btnConfirmar; // Value injected by FXMLLoader

    @FXML // fx:id="cbIdioma"
    private ComboBox<String> cbIdioma; // Value injected by FXMLLoader

    @FXML // fx:id="txtBD"
    private TextField txtBD; // Value injected by FXMLLoader

    @FXML // fx:id="txtContrasenia"
    private PasswordField txtContrasenia; // Value injected by FXMLLoader

    @FXML // fx:id="txtDireccion"
    private TextField txtDireccion; // Value injected by FXMLLoader

    @FXML // fx:id="txtPuerto"
    private TextField txtPuerto; // Value injected by FXMLLoader

    @FXML // fx:id="txtUsuario"
    private TextField txtUsuario; // Value injected by FXMLLoader

    private Properties db;

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        db = new Properties();
        cbIdioma.getItems().addAll("Español", "English");
        cbIdioma.getSelectionModel().select(0);
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Test". Prueba la conexión a la base de datos y activa/desactiva el botón de confirmar
     *
     * @param event evento del usuario
     */
    @FXML
    void probarConexion(ActionEvent event) {
        String error = validar();
        if (!error.isEmpty()) {
            btnConfirmar.setDisable(true);
            mostrarAlerta(error);
        } else {
            boolean valido = DBConnect.testConnection(txtDireccion.getText(), Integer.parseInt(txtPuerto.getText()), txtUsuario.getText(), txtContrasenia.getText(), txtBD.getText());
            if (valido) {
                mostrarConfirmacion("Connection valid / Conexión valida");
                btnConfirmar.setDisable(false);
            } else {
                btnConfirmar.setDisable(true);
                mostrarAlerta("Connection invalid, please try again / Conexión invalida, vuelva a intentarlo");
            }
        }
    }

    /**
     * Función que valida el formulario y devuelve los posibles errores
     *
     * @return string con posibles errores
     */
    private String validar() {
        String error = "";
        if (txtDireccion.getText().isEmpty()) {
            error += "The address field cannot be empty / El campo dirección no puede estar vacío\n";
        } else {
            db.setProperty("address", txtDireccion.getText());
        }
        if (txtPuerto.getText().isEmpty()) {
            error += "The port field cannot be empty / El campo puerto no puede estar vacío\n";
        } else if (!ValidadorNumero.validarInt(txtPuerto.getText())) {
            error += "The port field must be numeric / El campo puerto tiene que ser numérico\n";
        } else {
            db.setProperty("port", txtPuerto.getText());
        }
        if (txtUsuario.getText().isEmpty()) {
            error += "The user field cannot be empty / El campo usuario no puede estar vacío\n";
        } else {
            db.setProperty("user", txtUsuario.getText());
        }
        if (txtContrasenia.getText().isEmpty()) {
            error += "The password field cannot be empty / El campo contraseña no puede estar vacío\n";
        } else {
            db.setProperty("password", txtContrasenia.getText());
        }
        if (txtBD.getText().isEmpty()) {
            error += "The database field cannot be empty / El campo base de datos no puede estar vacío\n";
        } else {
            db.setProperty("database", txtBD.getText());
        }
        return error;
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
        DBConnect.createConfiguration(db);
        cerrar(null);
    }

    /**
     * Función que se ejecuta cuando se pulsa el menu item "Exit". Cierra la aplicación
     *
     * @param event evento del usuario
     */
    @FXML
    void cerrar(ActionEvent event) {
        Stage stage = (Stage) cbIdioma.getScene().getWindow();
        stage.close();
    }

    /**
     * Función que muestra un mensaje de confirmación al usuario
     *
     * @param mensaje de confirmación a mostrar
     */
    public void mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setTitle("Confirmar");
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
