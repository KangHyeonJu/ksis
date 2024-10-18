package com.boot.ksis.controller.notification;

import com.boot.ksis.dto.notification.AccountNotificationDTO;
import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.service.notification.NotificationService;
import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    // 페이지별 데이터 가져오는 요청
    @GetMapping("/notifications")
    public Page<AccountNotificationDTO> getPageNotifications(
            Principal principal,
            @RequestParam int page, // 페이지 번호
            @RequestParam int size  // 페이지당 항목 수
    ){
        String accountId = principal.getName();
        return notificationService.getPageNotifications(accountId, page, size);
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
