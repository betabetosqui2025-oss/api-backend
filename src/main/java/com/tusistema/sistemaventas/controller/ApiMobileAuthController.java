package com.tusistema.sistemaventas.controller;

import com.tusistema.sistemaventas.dto.*;
import com.tusistema.sistemaventas.model.Usuario;
import com.tusistema.sistemaventas.model.Cliente;
import com.tusistema.sistemaventas.model.Producto;
import com.tusistema.sistemaventas.model.Inventario;
import com.tusistema.sistemaventas.service.UsuarioService;
import com.tusistema.sistemaventas.util.JwtTokenUtil;
import com.tusistema.sistemaventas.repository.ClienteRepository;
import com.tusistema.sistemaventas.repository.ProductoRepository;
import com.tusistema.sistemaventas.repository.InventarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/mobile")
@CrossOrigin(origins = "*")
public class ApiMobileAuthController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    // ==================== ✅ AUTHENTICACIÓN ====================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.authenticateForMobile(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Credenciales inválidas o usuario inactivo"));
            }
            
            Usuario usuario = usuarioOpt.get();
            String token = jwtTokenUtil.generateToken(usuario);
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(true);
            loginResponse.setMessage("Login exitoso");
            loginResponse.setToken(token);
            loginResponse.setUserId(usuario.getId());
            loginResponse.setUsername(usuario.getUsername());
            loginResponse.setNombreCompleto(usuario.getNombreCompleto());
            loginResponse.setRoles(usuario.getRoles());
            loginResponse.setEnabled(usuario.isEnabled());
            loginResponse.setDemoUser(usuario.isDemoUser());
            
            return ResponseEntity.ok(ApiResponse.success(loginResponse));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error en el servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.ok(ApiResponse.error("Token inválido"));
            }
            
            String jwtToken = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
            
            if (!jwtTokenUtil.validateToken(jwtToken, username)) {
                return ResponseEntity.ok(ApiResponse.error("Token expirado o inválido"));
            }
            
            if (!usuarioService.userExistsForMobile(username)) {
                return ResponseEntity.ok(ApiResponse.error("Usuario no encontrado"));
            }
            
            Optional<Usuario> usuarioOpt = usuarioService.getUserForTokenVerification(username);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("Usuario inactivo"));
            }
            
            Usuario usuario = usuarioOpt.get();
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setSuccess(true);
            loginResponse.setMessage("Token válido");
            loginResponse.setToken(jwtToken);
            loginResponse.setUserId(usuario.getId());
            loginResponse.setUsername(usuario.getUsername());
            loginResponse.setNombreCompleto(usuario.getNombreCompleto());
            loginResponse.setRoles(usuario.getRoles());
            loginResponse.setEnabled(usuario.isEnabled());
            loginResponse.setDemoUser(usuario.isDemoUser());
            
            return ResponseEntity.ok(ApiResponse.success(loginResponse));
            
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error verificando token: " + e.getMessage()));
        }
    }

    // ==================== ✅ HEALTH CHECK ====================
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "✅ API Móvil funcionando correctamente");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    // ==================== 📦 PRODUCTOS ====================
    @GetMapping("/products")
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        try {
            List<Producto> productos = productoRepository.findByActivoTrue();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable String id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products/search/{nombre}")
    public ResponseEntity<List<Producto>> buscarProductos(@PathVariable String nombre) {
        try {
            List<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ==================== 👥 CLIENTES ====================
    @GetMapping("/clients")
    public ResponseEntity<List<Cliente>> obtenerClientes() {
        try {
            List<Cliente> clientes = clienteRepository.findAll();
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/clients/search/{nombre}")
    public ResponseEntity<List<Cliente>> buscarClientes(@PathVariable String nombre) {
        try {
            List<Cliente> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/clients")
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        try {
            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ResponseEntity.ok(clienteGuardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== 📱 INVENTARIO INTELIGENTE - KONTROL+ ====================

    // 🔍 BUSCAR PRODUCTO POR CÓDIGO DE BARRAS
    @GetMapping("/inventory/barcode/{codigoBarras}")
    public ResponseEntity<Producto> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        try {
            Optional<Producto> producto = productoRepository.findByCodigoBarras(codigoBarras);
            if (producto.isPresent()) {
                return ResponseEntity.ok(producto.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 📊 OBTENER STOCK COMPLETO DE PRODUCTO
    @GetMapping("/inventory/stock/{productoId}")
    public ResponseEntity<Map<String, Object>> obtenerStockProducto(@PathVariable String productoId) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Obtener producto
            Optional<Producto> productoOpt = productoRepository.findById(productoId);
            if (productoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Obtener inventario
            Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoId(productoId);
            int stock = inventarioOpt.map(Inventario::getCantidad).orElse(0);
            
            Producto producto = productoOpt.get();
            response.put("producto", producto);
            response.put("stock", stock);
            response.put("estado", getEstadoStock(stock));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 📈 OBTENER PRODUCTOS CON STOCK BAJO
    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<Map<String, Object>>> obtenerProductosStockBajo() {
        try {
            List<Inventario> inventariosBajos = inventarioRepository.findByCantidadLessThan(10);
            List<Map<String, Object>> resultado = new ArrayList<>();
            
            for (Inventario inventario : inventariosBajos) {
                Optional<Producto> productoOpt = productoRepository.findById(inventario.getProductoId());
                if (productoOpt.isPresent()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("producto", productoOpt.get());
                    item.put("stock", inventario.getCantidad());
                    item.put("estado", getEstadoStock(inventario.getCantidad()));
                    resultado.add(item);
                }
            }
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 🔄 BUSCAR PRODUCTO POR NOMBRE O CÓDIGO (BÚSQUEDA FLEXIBLE)
    @GetMapping("/inventory/search/{termino}")
    public ResponseEntity<List<Producto>> buscarProductosFlexible(@PathVariable String termino) {
        try {
            // Buscar por código de barras
            Optional<Producto> porCodigo = productoRepository.findByCodigoBarras(termino);
            if (porCodigo.isPresent()) {
                return ResponseEntity.ok(List.of(porCodigo.get()));
            }
            
            // Buscar por nombre
            List<Producto> porNombre = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(termino);
            return ResponseEntity.ok(porNombre);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 📋 OBTENER INVENTARIO COMPLETO
    @GetMapping("/inventory")
    public ResponseEntity<List<Inventario>> obtenerInventario() {
        try {
            List<Inventario> inventario = inventarioRepository.findAll();
            return ResponseEntity.ok(inventario);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Método auxiliar para determinar estado del stock
    private String getEstadoStock(int cantidad) {
        if (cantidad == 0) return "AGOTADO";
        if (cantidad < 5) return "STOCK_BAJO";
        if (cantidad < 10) return "STOCK_MEDIO";
        return "STOCK_NORMAL";
    }
}