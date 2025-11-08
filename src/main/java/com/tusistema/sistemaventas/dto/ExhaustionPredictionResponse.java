package com.tusistema.sistemaventas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para RECIBIR la respuesta de la API de Python.
 * Mapea la respuesta JSON (snake_case) a los campos de Java (camelCase).
 */
public record ExhaustionPredictionResponse(
    @JsonProperty("product_id")
    String productId,

    @JsonProperty("days_remaining")
    double daysRemaining
) {}