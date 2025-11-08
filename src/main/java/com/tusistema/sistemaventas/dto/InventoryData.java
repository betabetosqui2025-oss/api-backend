package com.tusistema.sistemaventas.dto;

/**
 * Representa los datos de inventario enviados al servicio Python.
 * Usa 'record' para DTOs simples (Java 14+).
 */
public record InventoryData(String productId, double currentStock) {}