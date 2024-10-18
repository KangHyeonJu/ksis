package com.boot.ksis.config;

import com.boot.ksis.handler.DeviceWebSocketHandler;
import com.boot.ksis.handler.LoginWebSocketHandler;
import com.boot.ksis.handler.MainWebSocketHandler;
import com.boot.ksis.handler.NotificationWebSocketHandler;
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
    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final LoginWebSocketHandler loginWebSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(deviceWebSocketHandler, "/ws/device")
                .setAllowedOrigins("*");

        registry.addHandler(mainWebSocketHandler, "/ws/main")
                .setAllowedOrigins("*");

        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
        .setAllowedOrigins("*");
        
        registry.addHandler(loginWebSocketHandler, "/ws/login")
                .setAllowedOrigins("*");
    }
}
