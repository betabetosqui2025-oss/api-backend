package com.tusistema.sistemaventas.controller;

import com.tusistema.sistemaventas.dto.InventarioVistaDTO;
import com.tusistema.sistemaventas.model.MovimientoInventario;
import com.tusistema.sistemaventas.model.Producto;
import com.tusistema.sistemaventas.repository.MovimientoInventarioRepository;
import com.tusistema.sistemaventas.repository.ProductoRepository;
import com.tusistema.sistemaventas.service.InventarioService; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 

import java.security.Principal; 
import java.util.List;

@Controller
@RequestMapping("/inventario")
@PreAuthorize("isAuthenticated()") 
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    private final InventarioService inventarioService;
    private final ProductoRepository productoRepository; 
    private final MovimientoInventarioRepository movimientoInventarioRepository; 

    public InventarioController(InventarioService inventarioService, 
                                ProductoRepository productoRepository,
                                MovimientoInventarioRepository movimientoInventarioRepository) {
        this.inventarioService = inventarioService;
        this.productoRepository = productoRepository;
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @GetMapping
    public String listarInventario(Model model) {
        List<InventarioVistaDTO> inventarioVista = inventarioService.obtenerInventarioVista();
        
        model.addAttribute("inventario", inventarioVista);
        model.addAttribute("pageTitle", "Gestión de Inventario");
        return "inventario/lista";
    }

    @PostMapping("/ajustar")
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    public String ajustarStock(@RequestParam("productoId") String productoId,
                             @RequestParam("cantidad") int cantidad,
                             @RequestParam("motivo") String motivo,
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
        
        if (principal == null) {
            return "redirect:/login";
        }
        
        try {
            // ============================================
            // ==     CORRECCIÓN APLICADA AQUÍ     ==
            // ============================================
            // Se reemplaza la constante 'TIPO_AJUSTE' por un texto simple.
            // (Tu modelo 'MovimientoInventario' no tiene esa constante)
            String tipoMovimiento = "AJUSTE_MANUAL"; 
            
            inventarioService.registrarMovimiento(
                productoId, 
                cantidad, 
                tipoMovimiento, // <-- CORREGIDO
                motivo, 
                principal.getName() 
            );
            redirectAttributes.addFlashAttribute("successMessage", "Stock actualizado correctamente.");
        } catch (Exception e) {
            logger.error("Error al ajustar stock para producto {}: {}", productoId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar stock: " + e.getMessage());
        }
        
        return "redirect:/inventario";
    }

    @GetMapping("/historial/{productoId}")
    public String verHistorialProducto(@PathVariable String productoId, Model model) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        List<MovimientoInventario> movimientos = movimientoInventarioRepository.findByProductoIdOrderByFechaDesc(productoId);

        model.addAttribute("producto", producto);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("pageTitle", "Historial: " + producto.getNombre());

        return "inventario/historial";
    }
}
