package com.alesandro.biblioteca.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase para formatear fechas
 */
public class FechaFormatter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /**
     * Función que formatea la fecha pasada como parámetro a string
     *
     * @param fecha a formatear
     * @return string de la fecha
     */
    public static String formatearString(LocalDateTime fecha) {
        return FORMATTER.format(fecha);
    }

    /**
     * Función que formatea el string pasado como parámetro a LocalDateTime
     *
     * @param str a formatear
     * @return fecha en formato LocalDateTime
     */
    public static LocalDateTime formatearFecha(String str) {
        return LocalDateTime.parse(str, FORMATTER);
    }
}
