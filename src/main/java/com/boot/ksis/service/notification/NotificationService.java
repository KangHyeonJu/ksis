package com.boot.ksis.service.notification;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.notification.NotificationUpdateDTO;
import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.dto.notification.AccountNotificationDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notification;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notification.NotificationRepository;
import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

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

    // 현재 페이지에 필요한 알림 데이터
    public Page<AccountNotificationDTO> getPageNotifications(String accountId, int page, int size){

        Account account = accountRepository.findById(accountId).orElseThrow(null);

        // 페이지 요청 생성
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "notificationId"));

        // 페이지네이션 적용된 쿼리 실행
        Page<Notification> notificationsPage = notificationRepository.findByAccount(account, pageRequest);

        // 결과를 DTO로 변환
        return notificationsPage.map(notification -> AccountNotificationDTO.builder()
                .notificationId(notification.getNotificationId())
                .isRead(notification.getIsRead())
                .message(notification.getMessage())
                .resourceType(notification.getResourceType())
                .build());
    }

    // 읽음상태로 업데이트
    public void isReadNotification(Long id){
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // 안읽은 알림 개수
    public int countNotification(String accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(null);

        return (int) notificationRepository.countByAccountAndIsRead(account, false);
    }

}
