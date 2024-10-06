package com.boot.ksis.controller.sse;

import com.boot.ksis.service.sse.SseEmitterService;
import com.boot.ksis.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterService emitterService;
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자가 연결을 요청할 때 호출되는 메소드
//    @GetMapping("/notifications")
    @GetMapping("/events")
    public SseEmitter connect(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = jwtTokenProvider.getAccountIdFromToken(token); // 토큰에서 사용자 ID 추출

            SseEmitter emitter = new SseEmitter(3600L * 1000L); // 새로운 SseEmitter 생성
            emitterService.addEmitter(userId, emitter); // 맵에 저장

            emitter.onCompletion(() -> {
                emitterService.removeEmitter(userId, emitter);
                System.out.println("SSE connection completed for user: " + userId);
            });
            emitter.onTimeout(() -> {
                emitter.complete(); // 타임아웃 시 연결 종료
                emitterService.removeEmitter(userId, emitter);
            });
            emitter.onError((e) -> {
                emitter.completeWithError(e); // 에러 발생 시 연결 종료
                emitterService.removeEmitter(userId, emitter);
            });

            return emitter;
        } else {
            throw new RuntimeException("Authorization token is missing or invalid");
        }
    }

    public final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();

    @GetMapping("/connect")
    public SseEmitter connect() {
        SseEmitter emitter = new SseEmitter(3600L * 1000L);
        String clientId = UUID.randomUUID().toString();
        clients.put(clientId, emitter);

        emitter.onCompletion(() -> clients.remove(clientId));
        emitter.onTimeout(() -> clients.remove(clientId));

        return emitter;
    }
}

