package com.alesandro.biblioteca.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Clase que se encarga de manejar los idiomas
 */
public class LanguageManager {
    private static LanguageManager instance;
    private Locale locale = new Locale.Builder().setLanguage(LanguageManager.getLanguage()).build();
    private ResourceBundle bundle;

    /**
     * Función que obtiene el idioma para la aplicación
     *
     * @return String con el idioma del fichero
     */
    public static String getLanguage() {
        File f = new File("lang.properties");
        Properties properties;
        try {
            FileInputStream configFileReader=new FileInputStream(f);
            properties = new Properties();
            try {
                properties.load(configFileReader);
                configFileReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            //throw new RuntimeException("lang.properties not found at config file path " + f.getPath());
            return null;
        }
        return properties.getProperty("language");
    }

    /**
     * Función que crea el fichero lang.properties si este no existe
     */
    public static void createFile(String language) {
        File f = new File("lang.properties");
        try {
            Properties properties = new Properties();

            // Modify the properties
            properties.setProperty("language", language);

            // Save the properties file
            FileOutputStream output = new FileOutputStream(f);
            properties.store(output, "Updated properties");
            output.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Función que cambia el idioma para la aplicación
     *
     * @param language string con el idioma del fichero
     */
    public static void setLanguage(String language) {
        File f = new File("lang.properties");
        try {
            Properties properties = new Properties();
            FileInputStream input = new FileInputStream(f);
            properties.load(input);
            input.close();

            // Modify the properties
            properties.setProperty("language", language);

            // Save the properties file
            FileOutputStream output = new FileOutputStream(f);
            properties.store(output, "Updated properties");
            output.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Constructor de la clase que carga el bundle
     */
    private LanguageManager() {
        loadResourceBundle();
    }

    /**
     * Crea una instancia de LanguageManager y la devuelve
     *
     * @return instancia de LanguageManager
     */
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    /**
     * Función que carga el bundle
     */
    private void loadResourceBundle() {
        bundle = ResourceBundle.getBundle("com/alesandro/biblioteca/languages/lang", locale);
    }

    /**
     * Setter de locale
     *
     * @param locale nuevo
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        loadResourceBundle();
    }

    /**
     * Getter de bundle
     *
     * @return bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Getter de locale
     *
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

}
