package com.voidsamuraj.HireLens.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures WebSocket messaging with STOMP (Simple Text Oriented Messaging Protocol), enables full-duplex
 * communication between client and server over WebSocket.
 * Enables a simple message broker and registers a STOMP endpoint with SockJS fallback.
 *
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker to handle messages sent to "/dataUpdate".
     *
     * @param registry the message broker registry to configure
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/dataUpdate");
       // registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers the STOMP endpoint at "/ws" with SockJS and allows all origins.
     *
     * @param registry the STOMP endpoint registry to configure
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
