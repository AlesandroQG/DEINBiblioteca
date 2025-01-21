package com.alesandro.biblioteca.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase para formatear fechas
 */
public class FechaFormatter {
    /**
     * Formateo de fechas
     */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /**
     * Función que formatea la fecha pasada como parámetro a string
     *
     * @param fecha a formatear
     * @return string de la fecha
     */
    public static String formatearFecha(LocalDateTime fecha) {
        return FORMATTER.format(fecha);
    }

    /**
     * Función que formatea el string pasado como parámetro a LocalDateTime
     *
     * @param string a formatear
     * @return fecha en formato LocalDateTime
     */
    public static LocalDateTime formatearString(String string) {
        return LocalDateTime.parse(string, FORMATTER);
    }
}
