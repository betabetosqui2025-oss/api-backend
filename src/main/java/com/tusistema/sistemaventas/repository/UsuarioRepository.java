package com.tusistema.sistemaventas.repository;

import com.tusistema.sistemaventas.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    
    // ✅ MÉTODOS COMPATIBLES (mantener)
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
    
    // ✅ QUERY PERSONALIZADO (mantener)
    @Query("{ 'username': ?0, 'enabled': true }")
    Optional<Usuario> findByUsernameAndEnabledTrue(String username);
    
    // ❌ ELIMINAR o COMENTAR métodos que requieren campo 'email'
    // Optional<Usuario> findByEmail(String email); 
    // Boolean existsByEmail(String email);
    
    // ✅ OPCIONAL: Agregar método para verificar usuario activo
    @Query(value = "{ 'username': ?0, 'enabled': true }", count = true)
    long countEnabledUsersByUsername(String username);
}