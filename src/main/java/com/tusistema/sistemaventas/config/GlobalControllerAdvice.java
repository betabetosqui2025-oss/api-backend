package com.tusistema.sistemaventas.config; // Asegúrate de que este sea tu paquete (o uno apropiado)

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Este método añade automáticamente el objeto HttpServletRequest actual
     * al modelo para todas las vistas renderizadas por los controladores.
     * Estará disponible en Thymeleaf bajo el nombre "request".
     *
     * @param request El HttpServletRequest actual inyectado por Spring.
     * @return El HttpServletRequest para ser añadido al modelo.
     */
    @ModelAttribute("request")
    public HttpServletRequest addRequestToModel(HttpServletRequest request) {
        return request;
    }
}
