package com.tusistema.sistemaventas.service;

import com.tusistema.sistemaventas.dto.InventarioVistaDTO;
import com.tusistema.sistemaventas.model.Inventario;
import com.tusistema.sistemaventas.model.MovimientoInventario;
import com.tusistema.sistemaventas.model.Producto;
import com.tusistema.sistemaventas.repository.InventarioRepository;
import com.tusistema.sistemaventas.repository.MovimientoInventarioRepository;
import com.tusistema.sistemaventas.repository.ProductoRepository;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional; // <-- LÍNEA QUITADA PARA DEPURAR

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository; 

    public InventarioService(InventarioRepository inventarioRepository,
                             MovimientoInventarioRepository movimientoRepository,
                             ProductoRepository productoRepository) { 
        this.inventarioRepository = inventarioRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository; 
    }

    /**
     * Lógica para obtener la vista de inventario basada en Productos.
     * Esto corrige el error de "Producto no encontrado" [cite: image_f49361.png].
     */
    public List<InventarioVistaDTO> obtenerInventarioVista() {
        // 1. Obtiene TODOS los productos (la fuente de verdad)
        List<Producto> productos = productoRepository.findAll();
        
        // 2. Obtiene TODO el inventario y lo pone en un Mapa para búsquedas rápidas
        Map<String, Inventario> inventarioMap = inventarioRepository.findAll().stream()
                .collect(Collectors.toMap(Inventario::getProductoId, inv -> inv));

        // 3. Itera sobre los PRODUCTOS, no sobre el inventario
        return productos.stream().map(producto -> {
            InventarioVistaDTO dto = new InventarioVistaDTO();
            dto.setProductoId(producto.getId()); 
            dto.setProductoNombre(producto.getNombre());
            dto.setProductoCategoria(producto.getCategoria());

            Inventario inventario = inventarioMap.get(producto.getId());
            
            if (inventario != null) {
                dto.setCantidadActual(inventario.getCantidad());
                dto.setFechaUltimaActualizacion(inventario.getFechaUltimaActualizacion());
            } else {
                dto.setCantidadActual(0);
                dto.setFechaUltimaActualizacion(null);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Método principal para cualquier actualización de stock.
     * Se quitó @Transactional para depurar el error del video [cite: dasdasd.mp4].
     */
    // @Transactional // <-- LÍNEA QUITADA
    public void registrarMovimiento(String productoId, int cantidad, String tipoMovimiento, String motivo, String username) {
        
        Inventario inventario = inventarioRepository.findByProductoId(productoId)
                .orElse(new Inventario(productoId, 0)); 

        int stockAnterior = inventario.getCantidad();
        int stockNuevo = stockAnterior + cantidad;

        if (stockNuevo < 0) {
            // Esto SÍ lanzará una excepción y el controlador la atrapará (lo cual es bueno)
            throw new RuntimeException("Stock insuficiente para el producto ID: " + productoId);
        }

        // ==================================
        // ==     CORRECCIÓN APLICADA     ==
        // ==================================
        
        // 1. Guardamos el inventario PRIMERO y de forma independiente.
        inventario.setCantidad(stockNuevo);
        inventario.setFechaUltimaActualizacion(LocalDateTime.now());
        inventarioRepository.save(inventario); // <-- Este guardado AHORA SÍ será permanente.

        // 2. Intentamos guardar el historial en un bloque try...catch separado.
        try {
            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setProductoId(productoId);
            movimiento.setCantidad(cantidad);
            movimiento.setTipoMovimiento(tipoMovimiento);
            movimiento.setMotivo(motivo);
            movimiento.setUsuarioId(username); // Guarda el username
            movimiento.setStockAnterior(stockAnterior);
            movimiento.setStockNuevo(stockNuevo);
            
            movimientoRepository.save(movimiento);
        } catch (Exception e) {
            // Si esto falla (el error silencioso), el stock YA SE ACTUALIZÓ.
            // Imprimimos el error en la consola de Java para saber por qué falló.
            System.err.println("--- ¡ERROR DEPURANDO! ---");
            System.err.println("El stock SÍ se actualizó, pero falló al guardar el MovimientoInventario.");
            System.err.println("Causa probable: El campo 'usuarioId' en 'MovimientoInventario' no acepta un String (username).");
            System.err.println("Error: " + e.getMessage());
            System.err.println("--------------------------");
        }
    }
    
    /**
     * Obtiene la cantidad de stock actual para un producto.
     */
    public int obtenerStockActual(String productoId) {
        return inventarioRepository.findByProductoId(productoId)
                .map(Inventario::getCantidad)
                .orElse(0);
    }
}

            