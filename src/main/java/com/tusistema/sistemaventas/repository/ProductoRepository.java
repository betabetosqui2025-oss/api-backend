package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends MongoRepository<Producto, String> {

    // ✅ MÉTODOS EXISTENTES (MANTENER)
    Optional<Producto> findByNombreIgnoreCase(String nombre);
    Optional<Producto> findByCodigoBarras(String codigoBarras);
    boolean existsByNombre(String nombre);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("{$or: [{'nombre': {$regex: ?0, $options: 'i'}}, {'codigoBarras': ?0}]}")
    List<Producto> buscarPorNombreOCodigo(String termino);

    // ✅ NUEVOS MÉTODOS PARA APP MÓVIL (AGREGAR)
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaAndActivoTrue(String categoria);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}