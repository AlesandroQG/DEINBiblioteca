package com.alesandro.biblioteca.utils;

/**
 * Clase que valida números
 */
public class ValidadorNumerico {
    /**
     * Función que valida un int
     *
     * @param valor en string
     * @return true/false
     */
    public static boolean validarInt(String valor) {
        try {
            Integer.parseInt(valor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
