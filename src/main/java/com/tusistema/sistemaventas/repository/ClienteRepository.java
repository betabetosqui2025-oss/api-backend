package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

    // Método para buscar un cliente por su número de documento.
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    // Método para buscar un cliente por su email.
    Optional<Cliente> findByEmail(String email);

    // Método para buscar clientes cuyo nombre o apellido contenga el término de búsqueda (ignorando mayúsculas/minúsculas).
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);

    // Método para verificar si existe un cliente con un número de documento específico.
    boolean existsByNumeroDocumento(String numeroDocumento);

    // Método para verificar si existe un cliente con un email específico.
    boolean existsByEmail(String email);

    // ✅ NUEVO MÉTODO PARA APP MÓVIL
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}