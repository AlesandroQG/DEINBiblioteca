package com.alesandro.biblioteca;

import com.alesandro.biblioteca.language.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Clase donde se ejecuta la aplicación principal
 *
 * @author Alesandro Quirós Gobbato
 */
public class BibliotecaApplication extends Application {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(BibliotecaApplication.class);

    /**
     * Función donde se carga y se muestra la ventana de la aplicación
     *
     * @param stage escena de la aplicación
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Comprobar si los ficheros de configuración requeridos existen
        File f1 = new File("lang.properties");
        File f2 = new File("configuration.properties");
        if (!f1.exists() || !f2.exists()) {
            logger.info("Starting initial configuration window");
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/FirstLaunch.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage1 = new Stage();
            stage1.setTitle("Welcome/Bienvenido");
            stage1.getIcons().add(new Image(getClass().getResourceAsStream("images/Biblioteca.png")));
            stage1.setResizable(false);
            stage1.setScene(scene);
            stage1.showAndWait();
        }
        // Vuelve a comprobar por si el usuario cierra la ventana
        f1 = new File("lang.properties");
        f2 = new File("configuration.properties");
        if (!f1.exists() || !f2.exists()) {
            logger.info("No configuration found, closing app");
            return;
        }
        // Abrir la aplicación
        logger.info("Starting main window");
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Main.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(bundle.getString("app.name"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/Biblioteca.png")));
        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Función main donde se lanza la aplicación
     *
     * @param args parámetros por consola
     */
    public static void main(String[] args) {
        Application.launch();
    }
}