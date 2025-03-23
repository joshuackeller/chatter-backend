package com.chatter.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.chatter.backend.handler.ChatWSHandler;
import com.chatter.backend.handler.WebSocketSessionManager;
import com.chatter.backend.service.ChatService;
import com.chatter.backend.service.KafkaMessageService;
import com.chatter.backend.utils.AuthHandshakeInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private AuthHandshakeInterceptor authHandshakeInterceptor;

    @Autowired
    private ChatService chatService;

    @Autowired
    private KafkaMessageService kafkaMessageService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWSHandler(objectMapper, chatService, kafkaMessageService, sessionManager), "/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*");
    }
}