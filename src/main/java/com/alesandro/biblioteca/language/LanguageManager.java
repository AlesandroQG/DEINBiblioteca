package com.alesandro.biblioteca.language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        HashMap<String,String> map = new HashMap<String,String>();
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
            throw new RuntimeException("lang.properties not found at config file path " + f.getPath());
        }
        return properties.getProperty("language");
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
