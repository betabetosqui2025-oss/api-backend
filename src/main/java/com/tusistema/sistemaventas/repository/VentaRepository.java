package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends MongoRepository<Venta, String> {

    Optional<Venta> findByNumeroFactura(String numeroFactura);
    
    List<Venta> findByClienteId(String clienteId);
    
    List<Venta> findByUsuarioId(String usuarioId);
    
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Venta> findByFechaVentaBetweenAndEstadoIn(LocalDateTime fechaInicio, LocalDateTime fechaFin, List<String> estados);

    // ✅ MÉTODO AÑADIDO PARA OPTIMIZAR DEVOLUCIONES
    List<Venta> findByEstadoIn(List<String> estados);
}