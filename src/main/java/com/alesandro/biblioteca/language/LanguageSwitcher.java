package com.alesandro.biblioteca.language;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase dedicada al cambio de idioma
 */
public class LanguageSwitcher {
    private Stage stage;

    /**
     * Constructor de la clase
     *
     * @param stage de la aplicación
     */
    public LanguageSwitcher(Stage stage) {
        this.stage = stage;
    }

    /**
     * Función que cambia el idioma de la aplicación
     *
     * @param locale nuevo locale
     */
    public void switchLanguage(Locale locale) {
        // Update the locale in the LanguageManager
        LanguageManager.getInstance().setLocale(locale);
        // Get the updated ResourceBundle
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        try {
            // Reload the FXML with the new ResourceBundle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/alesandro/biblioteca/fxml/Main.fxml"), bundle);
            Parent root = loader.load();
            stage.setTitle(bundle.getString("app.name"));
            // Update the scene with the new root (new language)
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
