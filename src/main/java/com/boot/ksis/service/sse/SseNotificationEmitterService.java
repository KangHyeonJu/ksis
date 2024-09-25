package com.boot.ksis.service.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseNotificationEmitterService {

    // 사용자별 SSE 연결을 관리하기 위한 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 사용자 추가
    public void addEmitter(String userId, SseEmitter emitter) {
        System.out.println("사용자 추가" + userId);
        if(emitters.containsKey(userId)){
            SseEmitter oldEmitter = emitters.get(userId);
            oldEmitter.complete();
            System.out.println("기존 sse 사용자 연결 종료" + userId);
        }
        emitters.put(userId, emitter);
    }

    // 사용자 제거
    public void removeEmitter(String userId) {
        System.out.println("사용자 제거" + userId);
        emitters.remove(userId);
    }

    // 사용자별 응답 보내는 메서드
    public void sendToUser(String userId, String message) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                System.out.println("Sending message to user: " + userId + " | Message: " + message);
                emitter.send(SseEmitter.event().data(message));
                System.out.println("Message sent successfully to user: " + userId);
            } catch (IOException e) {
                System.err.println("Failed to send message to user: " + userId + " | Error: " + e.getMessage());
                emitter.completeWithError(e);
            }
        }else{
            System.err.println("No emitter found for user: " + userId);
        }
    }
}
