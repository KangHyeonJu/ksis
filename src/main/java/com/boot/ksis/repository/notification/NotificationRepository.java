package com.boot.ksis.repository.notification;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 Account와 연관된 모든 Notification 조회
    List<Notification> findByAccount(Account account);

    // 특정 Account와 연관된 읽지 않은 Notification의 개수 조회
    long countByAccountAndIsRead(Account account, boolean isRead);
}
