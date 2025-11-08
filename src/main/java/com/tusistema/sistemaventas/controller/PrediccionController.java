package com.tusistema.sistemaventas.controller;

import com.tusistema.sistemaventas.dto.ExhaustionPredictionResponse; // <-- Ya tienes este DTO
import com.tusistema.sistemaventas.dto.PrediccionInventarioDTO;
import com.tusistema.sistemaventas.dto.PrediccionResultadoDTO;
import com.tusistema.sistemaventas.model.Producto;
import com.tusistema.sistemaventas.repository.ProductoRepository; 
import com.tusistema.sistemaventas.service.InventarioPrediccionService;
import com.tusistema.sistemaventas.service.PrediccionService;
import com.tusistema.sistemaventas.service.ProductoService; 
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prediccion")
public class PrediccionController {

    private final PrediccionService prediccionService;
    private final ProductoRepository productoRepository; 
    private final InventarioPrediccionService inventarioPrediccionService;
    private final ProductoService productoService; 

    @Autowired
    public PrediccionController(PrediccionService prediccionService,
                                ProductoRepository productoRepository,
                                InventarioPrediccionService inventarioPrediccionService,
                                ProductoService productoService) { 
        this.prediccionService = prediccionService;
        this.productoRepository = productoRepository;
        this.inventarioPrediccionService = inventarioPrediccionService;
        this.productoService = productoService; 
    }

    // --- (Tus métodos existentes mostrarPrediccion y exportarExcel se mantienen) ---
    @GetMapping
    public String mostrarPrediccion(
            // ... (Tus parámetros)
            Model model) {
        // ... (Tu lógica)
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(30);
        
        PrediccionResultadoDTO resultado = prediccionService.analizarYPredecir(fechaInicio, fechaFin, "DIARIA", null);
        model.addAttribute("resultado", resultado);
        model.addAttribute("productos", productoRepository.findAll());
        // ... (Más atributos)
        model.addAttribute("pageTitle", "Análisis y Predicción de Ventas"); 

        return "prediccion"; // Asume /templates/prediccion.html
    }

    @GetMapping("/exportar-excel")
    public void exportarExcel(
            // ... (Tus parámetros)
            HttpServletResponse response) throws IOException {
        // ... (Tu lógica)
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(30);
        prediccionService.generarExcelPrediccion(response, fechaInicio, fechaFin, "DIARIA", null);
    }

    @GetMapping("/inventario")
    public String mostrarPrediccionInventario(Model model) {
        List<PrediccionInventarioDTO> predicciones = inventarioPrediccionService.obtenerPrediccionesInventario();
        model.addAttribute("predicciones", predicciones);
        model.addAttribute("pageTitle", "Predicción de Inventario");
        return "inventario/inventario-prediccion";
    }


    // ============================================
    // == ENDPOINT PARA PREDICCIÓN PYTHON (TU CÓDIGO) ==
    // ============================================
    /**
     * Endpoint para mostrar la predicción de agotamiento de stock obtenida del servicio Python.
     * Esta lógica ya estaba en tu archivo y es correcta.
     */
    @GetMapping("/agotamiento") // Ruta: /prediccion/agotamiento
    public String mostrarPrediccionAgotamientoPython(Model model) {

        // 1. Obtener predicciones desde Python (via PrediccionService)
        List<ExhaustionPredictionResponse> predicciones = prediccionService.obtenerPrediccionAgotamientoPython();

        // 2. Obtener nombres de productos correspondientes
        List<String> productoIds = predicciones.stream()
                                            .map(ExhaustionPredictionResponse::productId)
                                            .collect(Collectors.toList());
        
        // Llama al NUEVO método en ProductoService
        Map<String, String> nombresProductos = productoService.obtenerProductosPorIds(productoIds).stream()
                                                    .collect(Collectors.toMap(Producto::getId, Producto::getNombre));

        // 3. Preparar datos para la vista (Map o DTO)
        List<Map<String, Object>> prediccionesVista = predicciones.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("nombreProducto", nombresProductos.getOrDefault(p.productId(), "ID: " + p.productId()));
            map.put("diasRestantes", p.daysRemaining());
            map.put("productoId", p.productId());
            return map;
        }).sorted(Comparator.comparingDouble(m -> (double) m.get("diasRestantes"))) // Ordenar por días
          .collect(Collectors.toList());

        // 4. Añadir al modelo
        model.addAttribute("predicciones", prediccionesVista);
        model.addAttribute("pageTitle", "Predicción de Agotamiento (Python)");

        // 5. Devolver nombre de la plantilla
        return "prediccion/prediccion-agotamiento"; //
    }
}