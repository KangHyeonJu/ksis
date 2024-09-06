package com.boot.ksis.repository.notification;

import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
