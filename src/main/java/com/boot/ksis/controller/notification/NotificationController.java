package com.boot.ksis.controller.notification;

import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.dto.notification.AccountNotificationDTO;
import com.boot.ksis.service.notification.NotificationService;
import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    // 원본 업로드 알림 데이터베이스 저장
    @PostMapping("/upload/notification")
    public void uploadNotification(@RequestBody UploadNotificationDTO uploadNotificationDTO){
        notificationService.uploadNotification(uploadNotificationDTO);
    }

    // 알림 데이터 가져오는 요청
    @GetMapping("/notifications")
    public List<AccountNotificationDTO> getAllNotifications(Principal principal){
        String accountId = principal.getName();

        return notificationService.getAllNotifications(accountId);
    }

    // 읽음표시로 데이터베이스 업데이트
    @PostMapping("/isRead/{notificationId}")
    public void isReadNotification(@PathVariable Long notificationId){
        notificationService.isReadNotification(notificationId);
    }

    // 알림 개수 가져오는 요청
    @GetMapping("/notifications/unread")
    public ResponseEntity<Integer> countNotification(Principal principal){
        String accountId = principal.getName();

        int unreadCount = notificationService.countNotification(accountId);

        return ResponseEntity.ok(unreadCount);
    }

    // Authorization 헤더에서 토큰을 추출하는 메서드
    private String resolveToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

}
