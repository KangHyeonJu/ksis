package com.boot.ksis.service.notification;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notification;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;

    // 원본 업로드 알림 데이터베이스 저장
    public void uploadNotification(UploadNotificationDTO uploadNotificationDTO){

        uploadNotificationDTO.setIsRead(false);

        Account account = accountRepository.findById(uploadNotificationDTO.getAccount())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String message = uploadNotificationDTO.getMessage() + " 업로드 성공";

        ResourceType resourceType = ResourceType.valueOf(uploadNotificationDTO.getResourceType());

        Notification notification = uploadNotificationDTO.createNewSignage();
        notification.setAccount(account);
        notification.setMessage(message);
        notification.setResourceType(resourceType);

        notificationRepository.save(notification);
    }
}
