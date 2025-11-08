package com.tusistema.sistemaventas.controller;

import com.tusistema.sistemaventas.model.Devolucion;
import com.tusistema.sistemaventas.model.DetalleDevolucion;
import com.tusistema.sistemaventas.model.Usuario;
import com.tusistema.sistemaventas.model.Venta;
import com.tusistema.sistemaventas.model.DetalleVenta; // Asegúrate de tener este import
import com.tusistema.sistemaventas.service.DevolucionService;
import com.tusistema.sistemaventas.service.VentaService;
import com.tusistema.sistemaventas.service.ProductoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set; // Importar Set si no estaba
import java.util.stream.Collectors;

@Controller
@RequestMapping("/devoluciones")
// --- 👇 ANOTACIÓN MODIFICADA PARA INCLUIR ROLE_DEMO ---
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR', 'ROLE_GESTOR', 'ROLE_DEMO')") 
public class DevolucionController {

    private static final Logger logger = LoggerFactory.getLogger(DevolucionController.class);

    private final DevolucionService devolucionService;
    private final VentaService ventaService;
    private final ProductoService productoService;

    @Autowired
    public DevolucionController(DevolucionService devolucionService,
                                  VentaService ventaService,
                                  ProductoService productoService) {
        this.devolucionService = devolucionService;
        this.ventaService = ventaService;
        this.productoService = productoService;
    }

    @GetMapping
    public String listarDevoluciones(Model model) {
        logger.info("Accediendo a listarDevoluciones");
        model.addAttribute("devoluciones", devolucionService.obtenerTodasLasDevoluciones());
        model.addAttribute("pageTitle", "Historial de Devoluciones");
        return "devoluciones/lista-devoluciones";
    }

    // Nota: El usuario DEMO probablemente no debería poder crear devoluciones.
    // Considera añadir @PreAuthorize aquí para restringir más.
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR', 'ROLE_GESTOR')") // Ejemplo
    @GetMapping("/nueva")
    public String mostrarFormularioSeleccionarVenta(Model model, @RequestParam(name = "ventaId", required = false) String ventaId) {
        // ... (resto del método sin cambios)
        return "devoluciones/seleccionar-venta-form";
    }

    @GetMapping("/api/venta-detalles/{ventaId}")
    @ResponseBody
    public ResponseEntity<?> obtenerDetallesVentaParaDevolucion(@PathVariable String ventaId) {
        // ... (resto del método sin cambios)
        Optional<Venta> ventaOpt = ventaService.obtenerVentaPorId(ventaId);
        return ventaOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Nota: El usuario DEMO probablemente no debería poder crear devoluciones.
    // Considera añadir @PreAuthorize aquí para restringir más.
    // @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_VENDEDOR', 'ROLE_GESTOR')") // Ejemplo
    @GetMapping("/nueva/detalles")
    public String mostrarFormularioNuevaDevolucion(@RequestParam("ventaId") String ventaId, Model model, RedirectAttributes redirectAttributes) {
        // ... (resto del método sin cambios)
        return "devoluciones/form-devolucion";
    }

    // Nota: El usuario DEMO probablemente no debería poder crear devoluciones.
    @PostMapping("/guardar")
    public String guardarDevolucion(@Valid @ModelAttribute("devolucion") Devolucion devolucion,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal Usuario usuarioLogueado,
                                      Model model, RedirectAttributes redirectAttributes) {
        
        // --- Añadir restricción específica para demo ---
         if (usuarioLogueado != null && usuarioLogueado.getRoles().contains("ROLE_DEMO")) {
             redirectAttributes.addFlashAttribute("errorMessage", "Los usuarios de demostración no pueden registrar devoluciones.");
             // Redirige a la lista de devoluciones (que sí puede ver)
             return "redirect:/devoluciones"; 
         }
        // --- Fin restricción ---

        // ... (resto de la lógica de validación y guardado sin cambios) ...
        
        try {
            devolucionService.procesarDevolucion(devolucion, devolucion.getVentaOriginalId(), usuarioLogueado.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Devolución registrada exitosamente.");
            return "redirect:/devoluciones";
        } catch (IllegalArgumentException e) {
            model.addAttribute("formErrorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("formErrorMessage", "Error inesperado al registrar la devolución: " + e.getMessage());
        }

        revalidarYPrepararModelo(devolucion, model); // Re-prepara el modelo en caso de error
        return "devoluciones/form-devolucion";
    }

    private void revalidarYPrepararModelo(Devolucion devolucion, Model model) {
        // ... (resto del método sin cambios)
    }

    @GetMapping("/{id}")
    public String verDetalleDevolucion(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
       // ... (resto del método sin cambios)
       Optional<Devolucion> devolucionOptional = devolucionService.obtenerDevolucionPorId(id);
       // ...
       return "devoluciones/detalle-devolucion";
    }
}