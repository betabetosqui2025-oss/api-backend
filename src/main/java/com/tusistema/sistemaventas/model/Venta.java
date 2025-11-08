package com.tusistema.sistemaventas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "ventas")
public class Venta {

    @Id
    private String id;
    private String numeroFactura;
    @NotNull(message = "La fecha de la venta es obligatoria.")
    private LocalDateTime fechaVenta;
    @NotNull(message = "El cliente es obligatorio para la venta.")
    private String clienteId;
    private String nombreCliente;
    @NotNull(message = "El usuario que registra la venta es obligatorio.")
    private String usuarioId;
    private String usernameUsuario;
    @NotEmpty(message = "Una venta debe tener al menos un detalle.")
    private List<DetalleVenta> detalles = new ArrayList<>();

    // ✅ CAMPOS AÑADIDOS PARA CORREGIR EL ERROR
    private BigDecimal subtotal;
    private BigDecimal montoIva;
    private Double porcentajeIva;
    // --- FIN DE CAMPOS AÑADIDOS ---

    @NotNull(message = "El total de la venta es obligatorio.")
    private BigDecimal totalVenta; // Este ya lo tenías
    private String estado;
    private String metodoPago;
    private String notas;

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_COMPLETADA = "COMPLETADA";
    public static final String ESTADO_CANCELADA = "CANCELADA";
    public static final String ESTADO_DEVUELTA_TOTAL = "DEVUELTA_TOTAL";
    public static final String ESTADO_DEVUELTA_PARCIAL = "DEVUELTA_PARCIAL";

    public Venta() {
        this.fechaVenta = LocalDateTime.now();
        this.totalVenta = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
        this.montoIva = BigDecimal.ZERO;
        this.porcentajeIva = 0.0;
        this.estado = ESTADO_PENDIENTE;
    }

    // --- Getters y Setters (Completos, incluyendo los nuevos) ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public String getUsernameUsuario() { return usernameUsuario; }
    public void setUsernameUsuario(String usernameUsuario) { this.usernameUsuario = usernameUsuario; }
    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    // ✅ GETTERS Y SETTERS PARA LOS NUEVOS CAMPOS
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getMontoIva() { return montoIva; }
    public void setMontoIva(BigDecimal montoIva) { this.montoIva = montoIva; }
    public Double getPorcentajeIva() { return porcentajeIva; }
    public void setPorcentajeIva(Double porcentajeIva) { this.porcentajeIva = porcentajeIva; }
    // --- FIN DE GETTERS Y SETTERS ---

    // El método `calcularTotalVenta` ya no es necesario aquí, la lógica se moverá al servicio.
}