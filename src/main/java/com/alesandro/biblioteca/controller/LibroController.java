package com.alesandro.biblioteca.controller;

import com.alesandro.biblioteca.model.Libro;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Clase controladora de la ventana libro
 */
public class LibroController implements Initializable {
    /**
     * Parámetro libro
     */
    private Libro libro;

    /**
     * Constructor con parámetros para la consulta o edición de un libro
     *
     * @param libro a consultar o editar
     */
    public LibroController(Libro libro) {
        this.libro = libro;
    }

    /**
     * Constructor vacío para la creación de un libro
     */
    public LibroController() {
        this.libro = null;
    }

    /**
     * Función que se ejecuta cuando se inicia la ventana
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }
}
