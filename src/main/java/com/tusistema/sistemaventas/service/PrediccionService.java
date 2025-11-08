package com.tusistema.sistemaventas.service;

import com.tusistema.sistemaventas.dto.ExhaustionPredictionResponse;
import com.tusistema.sistemaventas.dto.PredictionRequest;
import com.tusistema.sistemaventas.dto.PrediccionResultadoDTO;
import com.tusistema.sistemaventas.dto.SalesHistoryData;
import com.tusistema.sistemaventas.dto.VentaDiariaDTO;
import com.tusistema.sistemaventas.model.Inventario;
import com.tusistema.sistemaventas.repository.InventarioRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

// == IMPORTS REQUERIDOS PARA LA CORRECCIÓN ==
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators; 
import org.bson.Document; // <-- NUEVO IMPORT NECESARIO

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrediccionService {

    // --- PROPIEDADES ---
    private final WebClient predictionWebClient;
    private final InventarioRepository inventarioRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PrediccionService(
            WebClient predictionWebClient,
            InventarioRepository inventarioRepository,
            MongoTemplate mongoTemplate
    ) {
        this.predictionWebClient = predictionWebClient;
        this.inventarioRepository = inventarioRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // ============================================
    // == TUS MÉTODOS EXISTENTES (con lógica) ==
    // ============================================

    public PrediccionResultadoDTO analizarYPredecir(LocalDate fechaInicio, LocalDate fechaFin, String granularidad, String productoId) {
        System.out.println("Ejecutando lógica de predicción de ventas existente...");
        List<VentaDiariaDTO> ventas = obtenerVentasAgregadas(fechaInicio, fechaFin, granularidad, productoId);
        PrediccionResultadoDTO resultado = new PrediccionResultadoDTO();
        resultado.setPeriodos(ventas);
        // ... (Tu lógica para calcular KPIs y predicciones Java)
        return resultado;
    }

    public void generarExcelPrediccion(HttpServletResponse response, LocalDate fechaInicio, LocalDate fechaFin, String granularidad, String productoId) throws IOException {
        System.out.println("Ejecutando lógica de exportar a Excel...");
    }


    // ============================================
    // == NUEVOS MÉTODOS PARA PREDICCIÓN PYTHON ==
    // ============================================

    public List<ExhaustionPredictionResponse> obtenerPrediccionAgotamientoPython() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        LocalDate fechaInicio = LocalDate.now().minusDays(90);

        List<PredictionRequest> requests = inventarios.stream()
            .map(inv -> {
                List<SalesHistoryData> historial = getHistorialVentasAgrupado(inv.getProductoId(), fechaInicio);
                return new PredictionRequest(
                    inv.getProductoId(),
                    inv.getCantidad(),
                    historial
                );
            })
            .filter(req -> !req.sales_history().isEmpty())
            .collect(Collectors.toList());

        if (requests.isEmpty()) {
            return List.of();
        }

        try {
            return predictionWebClient.post()
                .uri("/predict/batch_exhaustion")
                .bodyValue(requests)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ExhaustionPredictionResponse>>() {})
                .block();
        } catch (Exception e) {
            System.err.println("Error al llamar a la API de predicción de Python: " + e.getMessage());
            return List.of();
        }
    }

    private List<SalesHistoryData> getHistorialVentasAgrupado(String productoId, LocalDate fechaInicio) {
        MatchOperation matchFecha = Aggregation.match(Criteria.where("fechaVenta").gte(fechaInicio.atStartOfDay()));
        UnwindOperation unwindDetalles = Aggregation.unwind("detalles");
        MatchOperation matchProducto = Aggregation.match(Criteria.where("detalles.productoId").is(productoId));
        
        // ==================================
        // ==     CORRECCIÓN APLICADA #1    ==
        // ==================================
        // Se reemplaza 'ConvertOperators' por un Documento BSON manual
        // para evitar el error de compilación [cite: image_f33681.png]
        ProjectionOperation projectCampos = Aggregation.project()
                .and(context -> new Document("$convert",
                        new Document("input", "$detalles.cantidad")
                                .append("to", "double")
                                .append("onError", 0.0)
                                .append("onNull", 0.0)
                )).as("cantidad")
                .andExpression("{$dateToString: {format: '%Y-%m-%d', date: '$fechaVenta'}}").as("fechaDiaria");
        
        GroupOperation groupPorDia = Aggregation.group("fechaDiaria")
                .sum("cantidad").as("totalVendido");
        
        ProjectionOperation projectAlDTO = Aggregation.project()
                .and("_id").as("ds")
                .and("totalVendido").as("y")
                .andExclude("_id");
        SortOperation sortPorFecha = Aggregation.sort(Sort.Direction.ASC, "ds");

        Aggregation agregacion = Aggregation.newAggregation(
            matchFecha, unwindDetalles, matchProducto, projectCampos, groupPorDia, projectAlDTO, sortPorFecha
        );

        AggregationResults<SalesHistoryData> resultados = mongoTemplate.aggregate(
            agregacion, "ventas", SalesHistoryData.class 
        );
        return resultados.getMappedResults();
    }


    // ========================================================
    // == MÉTODO QUE LANZÓ EL ERROR (AHORA CORREGIDO) ==
    // ========================================================

    private static class VentaDiariaAggDTO {
        public String periodo;
        public Long cantidadItems;
        public Double totalVenta;
    }

    public List<VentaDiariaDTO> obtenerVentasAgregadas(LocalDate fechaInicio, LocalDate fechaFin, String granularidad, String productoId) {

        MatchOperation matchFecha = Aggregation.match(
            Criteria.where("fechaVenta").gte(fechaInicio.atStartOfDay()).lt(fechaFin.plusDays(1).atStartOfDay())
        );
        UnwindOperation unwindDetalles = Aggregation.unwind("detalles");

        List<AggregationOperation> stages = new ArrayList<>();
        stages.add(matchFecha);
        stages.add(unwindDetalles);

        if (StringUtils.hasText(productoId)) {
            stages.add(Aggregation.match(Criteria.where("detalles.productoId").is(productoId)));
        }

        // ==================================
        // ==     CORRECCIÓN APLICADA #2    ==
        // ==================================
        // Se reemplaza 'ConvertOperators' por Documentos BSON manuales
        ProjectionOperation projectCampos = Aggregation.project()
                .and(context -> new Document("$convert",
                        new Document("input", "$detalles.cantidad")
                                .append("to", "double")
                                .append("onError", 0.0)
                                .append("onNull", 0.0)
                )).as("cantidad")
                .and(context -> new Document("$convert",
                        new Document("input", "$detalles.precioUnitario")
                                .append("to", "double")
                                .append("onError", 0.0)
                                .append("onNull", 0.0)
                )).as("precioUnitario")
                .andExpression("{$dateToString: {format: '%Y-%m-%d', date: '$fechaVenta'}}").as("periodo");

        GroupOperation groupPorPeriodo = Aggregation.group("periodo")
                .sum("cantidad").as("cantidadItems") 
                .sum(ArithmeticOperators.Multiply.valueOf("cantidad").multiplyBy("precioUnitario")).as("totalVenta"); 

        ProjectionOperation projectAlDTO = Aggregation.project()
                .and("_id").as("periodo")
                .and("cantidadItems").as("cantidadItems")
                .and("totalVenta").as("totalVenta")
                .andExclude("_id");

        SortOperation sortPorPeriodo = Aggregation.sort(Sort.Direction.ASC, "periodo");

        stages.add(projectCampos);
        stages.add(groupPorPeriodo);
        stages.add(projectAlDTO);
        stages.add(sortPorPeriodo);

        Aggregation agregacion = Aggregation.newAggregation(stages);

        AggregationResults<VentaDiariaAggDTO> resultados = mongoTemplate.aggregate(
            agregacion, "ventas", VentaDiariaAggDTO.class 
        );

        // Convertir a tu DTO final
        return resultados.getMappedResults().stream()
            .map(agg -> new VentaDiariaDTO(
                LocalDate.parse(agg.periodo), 
                BigDecimal.valueOf(agg.totalVenta != null ? agg.totalVenta : 0.0), 
                (agg.cantidadItems != null) ? agg.cantidadItems.intValue() : 0 
            ))
            .collect(Collectors.toList());
    }
}

