package com.tusistema.sistemaventas.service;

import com.tusistema.sistemaventas.dto.PrediccionInventarioDTO;
import com.tusistema.sistemaventas.dto.VentaDiariaDTO;
import com.tusistema.sistemaventas.model.Inventario;
import com.tusistema.sistemaventas.model.Producto;
import com.tusistema.sistemaventas.repository.InventarioRepository;
import com.tusistema.sistemaventas.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventarioPrediccionService {

    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    private final PrediccionService prediccionService; // Reutilizamos el servicio que ya creamos

    public InventarioPrediccionService(ProductoRepository productoRepository,
                                     InventarioRepository inventarioRepository,
                                     PrediccionService prediccionService) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.prediccionService = prediccionService;
    }

    public List<PrediccionInventarioDTO> obtenerPrediccionesInventario() {
        List<Producto> productos = productoRepository.findAll();
        
        // Creamos un mapa para buscar el stock de cada producto fácilmente
        // Asumo que tu entidad Inventario tiene los métodos getProductoId() y getCantidad()
        Map<String, Integer> stockMap = inventarioRepository.findAll().stream()
                .collect(Collectors.toMap(Inventario::getProductoId, Inventario::getCantidad));

        // Usaremos los últimos 90 días de ventas para un promedio robusto
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(90);

        return productos.stream().map(producto -> {
            PrediccionInventarioDTO dto = new PrediccionInventarioDTO();
            dto.setProductoId(producto.getId());
            dto.setNombreProducto(producto.getNombre());
            
            int stockActual = stockMap.getOrDefault(producto.getId(), 0);
            dto.setStockActual(stockActual);

            // 1. Obtenemos el historial de ventas para este producto
            List<VentaDiariaDTO> ventasDelProducto = prediccionService.obtenerVentasAgregadas(
                fechaInicio, fechaFin, "DIARIA", producto.getId()
            );

            // 2. Calculamos la venta promedio diaria de items
            double totalItemsVendidos = ventasDelProducto.stream()
                .mapToInt(VentaDiariaDTO::getCantidadItems)
                .sum();
            
            double ventaPromedioDiaria = (ventasDelProducto.isEmpty() || totalItemsVendidos == 0) ? 0 : totalItemsVendidos / 90.0;
            dto.setVentaPromedioDiaria(ventaPromedioDiaria);

            // 3. Calculamos los días restantes y la fecha de agotamiento
            if (ventaPromedioDiaria > 0) {
                long diasRestantes = Math.round(stockActual / ventaPromedioDiaria);
                dto.setDiasRestantes(diasRestantes);
                dto.setFechaAgotamiento(LocalDate.now().plusDays(diasRestantes));
                
                // 4. Determinamos el estado del inventario
                if (diasRestantes <= 7) {
                    dto.setEstado("Crítico");
                } else if (diasRestantes <= 30) {
                    dto.setEstado("Alerta");
                } else {
                    dto.setEstado("OK");
                }
            } else {
                dto.setDiasRestantes(9999); // Un valor alto para "infinito"
                dto.setEstado("OK");
            }

            return dto;
        }).collect(Collectors.toList());
    }
}