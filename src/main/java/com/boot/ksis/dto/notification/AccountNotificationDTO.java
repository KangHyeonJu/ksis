package com.boot.ksis.dto.notification;

import com.boot.ksis.constant.ResourceType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountNotificationDTO {

    private Long notificationId;
    private String message;
    private Boolean isRead;
    private ResourceType resourceType;
}
