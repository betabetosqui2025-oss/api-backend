package com.tusistema.sistemaventas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Permite todos los mensajes entrantes, crucialmente los de tipo CONNECT
            // para que la conexión se pueda establecer sin autenticación en este paso inicial.
            .anyMessage().permitAll();
            
            // Una configuración más estricta podría ser:
            // .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
            // .simpDestSubscribeMatchers("/topic/**").authenticated() // Requiere auth para suscribirse
            // .anyMessage().authenticated(); // Requiere auth para otros mensajes
    }

    /**
     * Desactiva la protección CSRF para los WebSockets.
     * Aunque ya está desactivado globalmente en tu SecurityConfig, es una buena práctica
     * ser explícito aquí para evitar problemas con las conexiones SockJS que pueden usar HTTP POST.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}