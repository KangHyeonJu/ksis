//package com.boot.ksis.config;
//
//import com.boot.ksis.entity.Device;
//import com.boot.ksis.repository.signage.SignageRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Component
//@RequiredArgsConstructor
//public class DeviceWebSocketHandler extends TextWebSocketHandler {
//    private final SignageRepository signageRepository;
//
//    @Override
//    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
//        updateDeviceConnectionStatus(session, true);
//    }
//
//    @Override
//    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
//        updateDeviceConnectionStatus(session, false);
//    }
//
//    public void updateDeviceConnectionStatus(WebSocketSession session, boolean isConnected){
//        String query = session.getUri().getQuery();
//        Long deviceId = Long.valueOf(extractDeviceIdFromQuery(query));
//
//        System.out.println("Connected Device ID: " + deviceId);
//
//        Device device = signageRepository.findByDeviceId(deviceId);
//
//        if (device != null) {
//            device.setIsConnect(isConnected);  // 연결 상태 업데이트
//            signageRepository.save(device);
//        }
//    }
//
//    private String extractDeviceIdFromQuery(String query) {
//        if (query != null && query.contains("deviceId=")) {
//            return query.split("=")[1]; // deviceId 값만 추출
//        }
//        return null;
//    }
//}
