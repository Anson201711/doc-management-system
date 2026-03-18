package com.docman.document.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置 - 支持实时文档协作
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单内存消息代理
        config.enableSimpleBroker("/topic", "/queue");
        // 客户端发送消息到服务端的前缀
        config.setApplicationDestinationPrefixes("/app");
        // 点对点消息前缀
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket连接端点
        registry.addEndpoint("/ws/document")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        // STOMP WebSocket端点
        registry.addEndpoint("/ws/document")
                .setAllowedOriginPatterns("*");
    }
}