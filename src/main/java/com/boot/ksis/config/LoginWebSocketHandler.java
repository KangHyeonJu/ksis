package com.boot.ksis.config;

import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoginWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject json = new JSONObject(payload);
        System.out.println("payload" + payload + "json" + json);

        if ("register".equals(json.getString("action"))) {
            String token = json.getString("token");
            try {
                String userId = jwtTokenProvider.getUserIdFromToken(token); // 사용자 ID 추출

                // 사용자 ID로 세션 리스트 관리
                sessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(session);

                System.out.println("웹 소켓 연결" + userId);
            } catch (Exception e) {
                session.sendMessage(new TextMessage("{\"error\": \"Invalid token\"}"));
                session.close(); // 세션 종료
            }
        } else if ("logout".equals(json.getString("action"))) {
            String token = json.getString("token");
            try {
                String userId = jwtTokenProvider.getUserIdFromToken(token); // 사용자 ID 추출
                List<WebSocketSession> clientSessions = sessions.get(userId);
                System.out.println("userId : " + userId);

                if (clientSessions != null) {
                    // 연결된 모든 세션에 로그아웃 메시지 전송
                    for (WebSocketSession clientSession : clientSessions) {
                        if (clientSession.isOpen()) {
                            clientSession.sendMessage(new TextMessage("{\"action\": \"logout\"}"));
                            clientSession.close(); // 세션 종료
                        }
                    }
                    sessions.remove(userId); // 세션 제거
                    System.out.println("로그아웃 아이디" + userId);
                }
            } catch (Exception e) {
                // 예외 처리 (예: 로그)
            }
        }
    }
}
