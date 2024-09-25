package com.boot.ksis.controller.sse;

import com.boot.ksis.service.sse.SseNotificationEmitterService;
import com.boot.ksis.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseNotificationController {

    private final SseNotificationEmitterService emitterService;
    private final JwtTokenProvider jwtTokenProvider;

    // 사용자가 연결을 요청할 때 호출되는 메소드
    @GetMapping("/notifications")
    public SseEmitter connect(HttpServletRequest request) {
        System.out.println("헤더에 담아온 정보" + request);
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String userId = jwtTokenProvider.getAccountIdFromToken(token); // 토큰에서 사용자 ID 추출

            SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // 새로운 SseEmitter 생성
            emitterService.addEmitter(userId, emitter); // 맵에 저장

            // 30초마다 빈 이벤트 전송
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    emitter.send(SseEmitter.event().comment("ping")); // 빈 이벤트 전송
                } catch (IOException e) {
                    emitterService.removeEmitter(userId);
                    emitter.completeWithError(e); // 연결을 에러와 함께 종료
                    scheduler.shutdown();
                }
            }, 0, 30, TimeUnit.SECONDS); // 0초 후 시작, 30초마다 실행

            emitter.onCompletion(() -> {
                System.out.println("SSE connection completed for user: " + userId);
             });
            emitter.onTimeout(() -> {
                emitter.complete(); // 타임아웃 시 연결 종료
                emitterService.removeEmitter(userId);
            });
            emitter.onError((e) -> {
                emitter.completeWithError(e); // 에러 발생 시 연결 종료
                emitterService.removeEmitter(userId);
            });

            return emitter;
        } else {
            throw new RuntimeException("Authorization token is missing or invalid");
        }
    }
}

