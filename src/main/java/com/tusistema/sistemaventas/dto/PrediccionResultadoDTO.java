package com.tusistema.sistemaventas.dto;

import java.util.List;

public class PrediccionResultadoDTO {

    // Datos para el gráfico
    private List<VentaDiariaDTO> periodos;
    
    // KPIs
    private double ingresosTotales;
    private double ventaPromedio;
    private int totalItems;
    private VentaDiariaDTO mejorPeriodo;
    
    // Predicciones
    private double prediccion1; // Para +1 periodo futuro
    private double prediccion2; // Para +2 periodos futuros

    // Getters y Setters
    public List<VentaDiariaDTO> getPeriodos() { return periodos; }
    public void setPeriodos(List<VentaDiariaDTO> periodos) { this.periodos = periodos; }
    public double getIngresosTotales() { return ingresosTotales; }
    public void setIngresosTotales(double ingresosTotales) { this.ingresosTotales = ingresosTotales; }
    public double getVentaPromedio() { return ventaPromedio; }
    public void setVentaPromedio(double ventaPromedio) { this.ventaPromedio = ventaPromedio; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public VentaDiariaDTO getMejorPeriodo() { return mejorPeriodo; }
    public void setMejorPeriodo(VentaDiariaDTO mejorPeriodo) { this.mejorPeriodo = mejorPeriodo; }
    public double getPrediccion1() { return prediccion1; }
    public void setPrediccion1(double prediccion1) { this.prediccion1 = prediccion1; }
    public double getPrediccion2() { return prediccion2; }
    public void setPrediccion2(double prediccion2) { this.prediccion2 = prediccion2; }
}