package com.tusistema.sistemaventas.controller;

import com.tusistema.sistemaventas.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/productos/images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    
    private final FileStorageService fileStorageService;

    @Autowired
    public ImageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * Servir imágenes de productos
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName, HttpServletRequest request) {
        try {
            logger.info("🔍 Solicitando imagen: {}", fileName);
            
            // Cargar el archivo como recurso
            Resource resource = fileStorageService.loadFileAsResource(fileName);
            
            // Determinar el tipo de contenido
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                logger.info("No se pudo determinar el tipo de contenido");
            }
            
            // Fallback para tipo de contenido
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            logger.info("✅ Imagen servida correctamente: {}", fileName);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
                    
        } catch (Exception e) {
            logger.error("❌ Error sirviendo imagen {}: {}", fileName, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}