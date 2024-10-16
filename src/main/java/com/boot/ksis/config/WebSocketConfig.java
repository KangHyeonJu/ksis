package com.boot.ksis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final DeviceWebSocketHandler deviceWebSocketHandler;
    private final MainWebSocketHandler mainWebSocketHandler;
    private final LoginWebSocketHandler loginWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deviceWebSocketHandler, "/ws/device")
                .setAllowedOrigins("*");

        registry.addHandler(mainWebSocketHandler, "/ws/main")
                .setAllowedOrigins("*");

        registry.addHandler(loginWebSocketHandler, "/ws/login")
                .setAllowedOrigins("*");
    }
}
