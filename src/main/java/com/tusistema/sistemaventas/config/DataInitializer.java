package com.tusistema.sistemaventas.config; // O el paquete que prefieras

import com.tusistema.sistemaventas.model.Usuario;
import com.tusistema.sistemaventas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador si no existe
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123")); // ¡Usa una contraseña segura!
            admin.setNombreCompleto("Administrador del Sistema");
            admin.setRoles(Set.of("ROLE_ADMIN", "ROLE_USER"));
            admin.setEnabled(true);
            usuarioRepository.save(admin);
            System.out.println("Usuario 'admin' creado.");
        }
               
        }
    }
