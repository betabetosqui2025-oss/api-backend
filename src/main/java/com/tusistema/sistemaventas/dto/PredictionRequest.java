package com.tusistema.sistemaventas.dto;

import java.util.List;

/**
 * DTO para ENVIAR la solicitud de predicción a la API de Python.
 */
public record PredictionRequest(
    String producto_id,
    int current_stock,
    List<SalesHistoryData> sales_history
) {}