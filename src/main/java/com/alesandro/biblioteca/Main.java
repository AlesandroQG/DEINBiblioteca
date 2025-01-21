package com.alesandro.biblioteca;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que ejecuta la aplicación (necesaria para el empaquetado a .jar)
 */
public class Main {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(Main.class.getName());
    /**
     * Función main donde se lanza la aplicación
     *
     * @param args parámetros por consola
     */
    public static void main(String[] args) {
        logger.info("Iniciando aplicación");
        BibliotecaApplication.main(args);
    }
}
