package com.tusistema.sistemaventas.dto;

import java.time.LocalDateTime;

public class InventarioVistaDTO {

    private String productoId;
    private String productoNombre;
    private String productoCategoria;
    private int cantidadActual;
    private LocalDateTime fechaUltimaActualizacion;

    // Getters y Setters
    public String getProductoId() { return productoId; }
    public void setProductoId(String productoId) { this.productoId = productoId; }
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    public String getProductoCategoria() { return productoCategoria; }
    public void setProductoCategoria(String productoCategoria) { this.productoCategoria = productoCategoria; }
    public int getCantidadActual() { return cantidadActual; }
    public void setCantidadActual(int cantidadActual) { this.cantidadActual = cantidadActual; }
    public LocalDateTime getFechaUltimaActualizacion() { return fechaUltimaActualizacion; }
    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) { this.fechaUltimaActualizacion = fechaUltimaActualizacion; }
    
    // Método toString() para ayudar en la depuración que hicimos
    @Override
    public String toString() {
        return "InventarioVistaDTO{" +
                "productoId='" + productoId + '\'' +
                ", productoNombre='" + productoNombre + '\'' +
                ", cantidadActual=" + cantidadActual +
                '}';
    }
}