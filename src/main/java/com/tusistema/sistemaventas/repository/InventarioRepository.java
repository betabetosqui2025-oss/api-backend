package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Inventario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends MongoRepository<Inventario, String> {

    // Método para encontrar fácilmente el inventario de un producto específico
    Optional<Inventario> findByProductoId(String productoId);

    // ✅ MÉTODO PARA INVENTARIO INTELIGENTE
    List<Inventario> findByCantidadLessThan(Integer cantidad);
    
    // ✅ MÉTODO PARA OBTENER TODO EL INVENTARIO
    List<Inventario> findAll();
}