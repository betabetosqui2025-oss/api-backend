package com.tusistema.sistemaventas.dto;

import java.util.List;

/**
 * Representa el cuerpo JSON completo de la solicitud POST al endpoint Python.
 */
public record ExhaustionPredictionRequest(List<InventoryData> inventory, List<SalesHistoryData> salesHistory) {}