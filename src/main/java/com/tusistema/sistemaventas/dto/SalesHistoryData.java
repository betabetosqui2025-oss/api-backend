package com.tusistema.sistemaventas.dto;

import java.time.LocalDate;

/**
 * Representa un registro de venta histórica para el servicio de predicción.
 * Prophet (la librería de IA) requiere los campos 'ds' (fecha) y 'y' (valor).
 */
public record SalesHistoryData(String ds, int y) {

    /**
     * Constructor auxiliar para que la agregación de Mongo
     * (que devuelve LocalDate y Long) pueda crear este DTO.
     */
    public SalesHistoryData(LocalDate date, Long quantity) {
        this(date.toString(), quantity.intValue());
    }
}