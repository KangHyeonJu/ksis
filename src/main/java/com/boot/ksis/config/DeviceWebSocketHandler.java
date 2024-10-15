package com.boot.ksis.config;

import com.boot.ksis.entity.Device;
import com.boot.ksis.repository.signage.SignageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceWebSocketHandler extends TextWebSocketHandler {
    private final SignageRepository signageRepository;
    private final MainWebSocketHandler mainWebSocketHandler;
    private static final Logger logger = LoggerFactory.getLogger(DeviceWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessions.add(session);
        updateDeviceConnectionStatus(session, true);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        sessions.remove(session);
        updateDeviceConnectionStatus(session, false);
    }

    public void updateDeviceConnectionStatus(WebSocketSession session, boolean isConnected){
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        Long deviceId = Long.valueOf(extractDeviceIdFromQuery(query));

        System.out.println("Connected Device ID: " + deviceId);

        Device device = signageRepository.findByDeviceId(deviceId);

        if (device != null) {
            device.setIsConnect(isConnected);  // 연결 상태 업데이트
            signageRepository.save(device);

            mainWebSocketHandler.sendStatusUpdateMessage();
        }
    }

    private String extractDeviceIdFromQuery(String query) {
        if (query != null && query.contains("deviceId=")) {
            return query.split("=")[1]; // deviceId 값만 추출
        }
        return null;
    }

    public void sendPlaylistUpdateMessage(Long signageId){
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    String query = Objects.requireNonNull(session.getUri()).getQuery();
                    Long deviceId = Long.valueOf(extractDeviceIdFromQuery(query));

                    if(Objects.equals(signageId, deviceId)){
                        session.sendMessage(new TextMessage("playlistUpdate"));
                    }
                } catch (IOException e) {
                    logger.error("Failed to send message to client: {}", session.getId(), e);
                }
            }
        }
    }

    public void sendNoticeUpdateMessage(List<Long> signageIds){
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    String query = Objects.requireNonNull(session.getUri()).getQuery();
                    Long deviceId = Long.valueOf(extractDeviceIdFromQuery(query));

                    if(signageIds.contains(deviceId)){
                        session.sendMessage(new TextMessage("noticeUpdate"));
                    }
                } catch (IOException e) {
                    logger.error("Failed to send message to client: {}", session.getId(), e);
                }
            }
        }
    }

    public void sendNoticeMessage(Long signageIds){
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    String query = Objects.requireNonNull(session.getUri()).getQuery();
                    Long deviceId = Long.valueOf(extractDeviceIdFromQuery(query));

                    if(signageIds.equals(deviceId)){
                        session.sendMessage(new TextMessage("noticeUpdate"));
                    }
                } catch (IOException e) {
                    logger.error("Failed to send message to client: {}", session.getId(), e);
                }
            }
        }
    }
}
