package com.alesandro.biblioteca;

import com.alesandro.biblioteca.language.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Clase donde se ejecuta la aplicación principal
 *
 * @author alesandroquirosgobbato
 */
public class BibliotecaApplication extends Application {
    /**
     * Función donde se carga y se muestra la ventana de la aplicación
     *
     * @param stage escena de la aplicación
     */
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/Main.fxml"), bundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(bundle.getString("app.name"));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/Biblioteca.png")));
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