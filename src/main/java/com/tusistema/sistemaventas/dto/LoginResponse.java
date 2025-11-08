package com.tusistema.sistemaventas.dto;

import java.util.Set;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String userId;
    private String username;
    private String nombreCompleto;
    private Set<String> roles; // ✅ CAMBIADO: de List<String> rules a Set<String> roles
    private boolean enabled;
    private boolean demoUser;
    
    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public Set<String> getRoles() { return roles; } // ✅ CAMBIADO
    public void setRoles(Set<String> roles) { this.roles = roles; } // ✅ CAMBIADO
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isDemoUser() { return demoUser; }
    public void setDemoUser(boolean demoUser) { this.demoUser = demoUser; }
}