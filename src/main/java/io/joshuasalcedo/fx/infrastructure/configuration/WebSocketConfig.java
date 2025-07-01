package io.joshuasalcedo.fx.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    // Enable a simple in-memory message broker
    config.enableSimpleBroker("/topic");
    // Set prefix for messages bound for @MessageMapping methods
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Register STOMP endpoint for WebSocket connections
    registry
        .addEndpoint("/ws-clipboard")
        .setAllowedOriginPatterns("*") // Configure based on your security requirements
        .withSockJS(); // Enable SockJS fallback
  }
}
