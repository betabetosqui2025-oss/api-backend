package com.tusistema.sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VentaDiariaDTO {

    private LocalDate fecha;
    private BigDecimal totalVendido; // ✅ CAMBIO: de double a BigDecimal
    private int cantidadItems;

    public VentaDiariaDTO(LocalDate fecha, BigDecimal totalVendido, int cantidadItems) {
        this.fecha = fecha;
        this.totalVendido = totalVendido;
        this.cantidadItems = cantidadItems;
    }

    // Getters y Setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BigDecimal getTotalVendido() { return totalVendido; }
    public void setTotalVendido(BigDecimal totalVendido) { this.totalVendido = totalVendido; }
    public int getCantidadItems() { return cantidadItems; }
    public void setCantidadItems(int cantidadItems) { this.cantidadItems = cantidadItems; }
}

