package com.tusistema.sistemaventas.service;

import com.tusistema.sistemaventas.model.Usuario;
import com.tusistema.sistemaventas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === MÉTODOS BÁSICOS EXISTENTES ===
    
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    // Método simplificado (sin contraseña)
    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void eliminarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existePorUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    // === MÉTODOS PARA EL CONTROLADOR WEB (NUEVOS) ===

    /**
     * Obtener todos los roles disponibles
     */
    @Transactional(readOnly = true)
    public List<String> obtenerTodosLosRolesDisponibles() {
        return Arrays.asList("ROLE_ADMIN", "ROLE_VENDEDOR", "ROLE_DEMO");
    }

    /**
     * Guardar usuario con contraseña (para formulario web)
     */
    @Transactional
    public Usuario guardarUsuario(Usuario usuario, String rawPassword) {
        // Si se proporciona una nueva contraseña, encriptarla
        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(rawPassword));
        }
        
        // Si no tiene roles, asignar rol por defecto
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.setRoles(Set.of("ROLE_VENDEDOR"));
        }
        
        // Validar que esté habilitado
        if (!usuario.isEnabled()) {
            usuario.setEnabled(true);
        }
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Verificar si existe usuario por email
     */
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Buscar por email en la lista de usuarios
        return usuarioRepository.findAll().stream()
                .anyMatch(usuario -> email.equals(usuario.getEmail()));
    }

    // === MÉTODOS PARA API MÓVIL ===

    @Transactional(readOnly = true)
    public Optional<Usuario> authenticateForMobile(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsernameAndEnabledTrue(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public boolean userExistsForMobile(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true) 
    public Optional<Usuario> getUserForTokenVerification(String username) {
        return usuarioRepository.findByUsernameAndEnabledTrue(username);
    }

    // === MÉTODO DEMO ===
    
    @Transactional
    public Usuario createAndSaveDemoUser() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String demoUsername = "demo_" + uniqueSuffix;
        String demoPassword = "demo123";

        Usuario demoUser = new Usuario();
        demoUser.setUsername(demoUsername);
        demoUser.setPassword(passwordEncoder.encode(demoPassword));
        demoUser.setNombreCompleto("Usuario Demo " + uniqueSuffix);
        demoUser.setEmail(demoUsername + "@demo.com");
        demoUser.setEnabled(true);
        demoUser.setDemoUser(true);
        demoUser.setDemoExpiryTime(LocalDateTime.now().plusHours(2));
        demoUser.setRoles(Set.of("ROLE_DEMO"));

        Usuario savedDemoUser = usuarioRepository.save(demoUser);
        logger.info("Usuario Demo creado: {}", savedDemoUser.getUsername());
        return savedDemoUser;
    }
}