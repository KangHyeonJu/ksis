package com.boot.ksis.controller.sse;

import com.boot.ksis.service.sse.SseNotificationEmitterService;
import com.boot.ksis.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

            emitter.onCompletion(() -> emitterService.removeEmitter(userId));
            emitter.onTimeout(() -> emitterService.removeEmitter(userId));
            emitter.onError((e) -> emitterService.removeEmitter(userId));

            return emitter;
        } else {
            throw new RuntimeException("Authorization token is missing or invalid");
        }
    }
}

