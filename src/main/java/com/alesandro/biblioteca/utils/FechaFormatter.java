package com.alesandro.biblioteca.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase para formatear fechas
 */
public class FechaFormatter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    /**
     * Función que formatea la fecha pasada como parámetro a string
     *
     * @param fecha a formatear
     * @return string de la fecha
     */
    public static String formatear(LocalDateTime fecha) {
        return formatter.format(fecha);
    }
}
