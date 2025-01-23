package com.alesandro.biblioteca.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase para validar números
 */
public class ValidadorNumero {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(ValidadorNumero.class.getName());

    /**
     * Función que valida un string para ver si se puede pasar a int
     *
     * @param numero a validar
     * @return true/false
     */
    public static boolean validarInt(String numero) {
        try {
            Integer.parseInt(numero);
            logger.info("String {} pasada a integer", numero);
            return true;
        } catch (NumberFormatException e) {
            logger.info("String {} no se puede pasar a integer", numero);
            return false;
        }
    }
}
