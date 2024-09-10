package com.boot.ksis.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class EventController {

    private final List<SseEmitter> emitters = new ArrayList<>();

    @GetMapping("/events")
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(3600L * 1000L); // 1시간 타임아웃
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            SecurityContextHolder.clearContext(); // SecurityContext 정리
        });

        emitter.onTimeout(() -> {
            emitter.complete();
            SecurityContextHolder.clearContext(); // 타임아웃 시 SecurityContext 정리
        });

        emitter.onError((e) -> {
            emitters.remove(emitter);
            emitter.completeWithError(e);
            SecurityContextHolder.clearContext(); // 에러 시 SecurityContext 정리
            System.err.println("SSE error: " + e.getMessage());
        });

        return emitter;
    }

    public void sendLogoutEvent(String accountId) {
        System.out.println("received event : " + accountId);

        List<SseEmitter> currentEmitters = new ArrayList<>(emitters);
        for (SseEmitter emitter : currentEmitters) {
            try {
                emitter.send(SseEmitter.event().name("logout").data(accountId));
                emitter.complete();
                System.out.println("Logout event sent and SSE connection closed for: " + emitter);
            } catch (IOException e) {
                System.err.println("Error occurred while sending event: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error occurred while sending event: " + e.getMessage());
            } finally {
                emitters.remove(emitter);
            }
        }
    }
}