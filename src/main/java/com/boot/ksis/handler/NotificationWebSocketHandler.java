package com.boot.ksis.handler;

import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    // 사용자 ID와 WebSocket 세션을 매핑하는 Map
    private static final ConcurrentHashMap<String, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();

    private final JwtTokenProvider jwtTokenProvider;

    // WebSocket 연결이 설정되었을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        String userId = getUserIdFromSession(session); // 세션에서 사용자 ID 추출
        if(userId != null){
            USER_SESSIONS.put(userId, session); // 사용자 ID와 세션을 매핑
        }else{
            session.close(CloseStatus.NOT_ACCEPTABLE); // 사용자 인증 실패 시 연결 종료
        }
    }

    // WebSocket 연결이 종료되었을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        String userId = getUserIdFromSession(session);
        if(userId != null){
            USER_SESSIONS.remove(userId); // 세션이 종료되면 해당 사용자 세션 제거
        }
    }

    // 사용자에게 알림을 전송하는 메서드
    public void sendToUser(String userId, String message){
        WebSocketSession session = USER_SESSIONS.get(userId);
        if(session != null && session.isOpen()){
            try{
                session.sendMessage(new TextMessage(message));
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("사용자를 찾지 못함: " + userId);
        }
    }

    // WebSocket 세션에서 사용자 ID 추출
    private String getUserIdFromSession(WebSocketSession session){
        String query = session.getUri().getQuery();
        if(query != null && query.contains("token")){
            String token = query.split("token=")[1]; // URL 쿼리에서 토큰 추출
            if(jwtTokenProvider.validateToken(token)){
                return jwtTokenProvider.getAccountIdFromToken(token); // JWT에서 사용자 ID 추출
            }
        }
        return null;  // 사용자 ID 추출 실패 시 null 반환
    }

}
