package com.boot.ksis.service.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitterService {

//     사용자별 SSE 연결을 관리하기 위한 맵
//    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    // 사용자 추가
//    public void addEmitter(String userId, SseEmitter emitter) {
//        System.out.println("사용자 추가" + userId);
//        emitters.put(userId, emitter);
//    }

    // 사용자 제거
//    public void removeEmitter(String userId) {
//        System.out.println("사용자 제거" + userId);
//        emitters.remove(userId);
//    }

    public void addEmitter(String userId, SseEmitter emitter) {
        // 계정아이디로 된 emitter가 없으면 리스트 생성
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));
    }

    public void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }


    // 사용자별 응답 보내는 메서드
//    public void sendToUser(String userId, String message) {
//        SseEmitter emitter = emitters.get(userId);
//        if (emitter != null) {
//            try {
//                System.out.println("Sending message to user: " + userId + " | Message: " + message);
//                emitter.send(SseEmitter.event().data(message));
//                System.out.println("Message sent successfully to user: " + userId);
//            } catch (IOException e) {
//                System.err.println("Failed to send message to user: " + userId + " | Error: " + e.getMessage());
//                emitter.completeWithError(e);
//            }
//        }else{
//            System.err.println("No emitter found for user: " + userId);
//        }
//    }

    public void sendToUser(String userId, String message) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().data(message));
                } catch (IOException e) {
                    removeEmitter(userId, emitter);
                }
            });
        }
    }

    public void sendLogoutEvent(String accountId) {
//        SseEmitter emitter = emitters.get(accountId);
        List<SseEmitter> userEmitters = emitters.get(accountId);
        if (userEmitters != null) {
            userEmitters.forEach(emitter -> {
                try {
                    System.out.println("Sending logout event to user: " + accountId);
                    emitter.send(SseEmitter.event().name("logout").data(accountId)); // 로그아웃 이벤트 전송
                    emitter.complete(); // 이벤트 전송 후 SSE 연결 종료
                    System.out.println("Logout event sent and SSE connection closed for user: " + accountId);
                } catch (IOException e) {
                    System.err.println("Failed to send logout event to user: " + accountId + " | Error: " + e.getMessage());
                    emitter.completeWithError(e); // 에러 발생 시 SSE 연결 종료
                } finally {
                    emitters.remove(accountId); // 로그아웃 후 해당 사용자에 대한 Emitter 제거
                }
            });
        } else {
            System.err.println("No emitter found for user: " + accountId);
        }
    }
}
