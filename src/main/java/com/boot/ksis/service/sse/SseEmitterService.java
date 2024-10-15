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

    public void sendLogoutEvent(String accountId) {
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
                }
            });
        } else {
            System.err.println("No emitter found for user: " + accountId);
        }
    }

    private final Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String clientId){
        SseEmitter emitter = new SseEmitter(3600L * 1000L);
        sseEmitterMap.put(clientId, emitter);

        emitter.onCompletion(() -> {
            sseEmitterMap.remove(clientId);
            System.out.println("SSE connection completed for client: " + clientId);
        });

        emitter.onTimeout(() -> {
            sseEmitterMap.remove(clientId);
            System.out.println("SSE connection timed out for client: " + clientId);
        });

        return emitter;
    }
    public void sendUpdateEvent(){
        for(SseEmitter emitter : sseEmitterMap.values()){
            try{
                System.out.println("Sending update event to client.");
                emitter.send(SseEmitter.event().data("재생목록 수정"));
            }catch (IOException e){
                System.out.println("Error sending event, removing emitter.");
                emitter.complete();
                sseEmitterMap.values().remove(emitter);
            }
        }
    }
}
