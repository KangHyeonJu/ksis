package com.boot.ksis.service.notification;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.notification.NotificationUpdateDTO;
import com.boot.ksis.dto.notification.UploadNotificationDTO;
import com.boot.ksis.dto.notification.AccountNotificationDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notification;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AccountRepository accountRepository;
    // 사용자별 SseEmitter를 저장하는 맵
    private final ConcurrentMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

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

        // sse로 업데이트 전송
        sendNotificationUpdate(account.getAccountId());
    }

    // 알림 데이터베이스 전체 데이터 가져오기
    public List<AccountNotificationDTO> getAllNotifications(String accountId){
        List<AccountNotificationDTO> dtos = new ArrayList<>();

        Account account = accountRepository.findById(accountId).orElseThrow(null);

        List<Notification> notifications = notificationRepository.findByAccount(account);

        for(Notification notification : notifications){
            AccountNotificationDTO accountNotificationDTO = AccountNotificationDTO.builder()
                    .notificationId(notification.getNotificationId())
                    .isRead(notification.getIsRead())
                    .message(notification.getMessage())
                    .resourceType(notification.getResourceType())
                    .build();

            dtos.add(accountNotificationDTO);
        }

        return dtos;
    }

    // 읽음상태로 업데이트
    public void isReadNotification(Long id){
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);

        // sse로 업데이트 전송
        sendNotificationUpdate(notification.getAccount().getAccountId());
    }

    // 안읽은 알림 개수
    public int countNotification(String accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(null);

        return (int) notificationRepository.countByAccountAndIsRead(account, false);
    }

    // SSE 이벤트 전송
    private void sendNotificationUpdate(String accountId) {
        int unreadCount = countNotification(accountId);
        SseEmitter emitter = emitters.get(accountId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(new NotificationUpdateDTO(unreadCount)));
            } catch (Exception e) {
                emitters.remove(accountId);
            }
        }
    }

    // SseEmitter 추가
    public SseEmitter addEmitter(String accountId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(accountId, emitter);

        emitter.onCompletion(() -> emitters.remove(accountId));
        emitter.onTimeout(() -> emitters.remove(accountId));
        emitter.onError((e) -> emitters.remove(accountId));

        return emitter;
    }
}
