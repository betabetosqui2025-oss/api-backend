package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Devolucion;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DevolucionRepository extends MongoRepository<Devolucion, String> {

    List<Devolucion> findByVentaOriginalId(String ventaOriginalId);
    List<Devolucion> findByClienteId(String clienteId);

    // --- INICIO: Nuevo método ---
    List<Devolucion> findByFechaDevolucionBetweenAndEstadoDevolucionIn(LocalDateTime fechaInicio, LocalDateTime fechaFin, List<String> estadosDevolucion);
    // --- FIN: Nuevo método ---
}