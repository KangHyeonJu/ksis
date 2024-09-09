package com.boot.ksis.controller.notification;

import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    // 원본 업로드 알림 데이터베이스 저장
    @PostMapping("/upload/notification")
    public void uploadNotification(@RequestBody UploadNotificationDTO uploadNotificationDTO){
        notificationService.uploadNotification(uploadNotificationDTO);
    }

}
