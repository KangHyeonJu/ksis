package com.boot.ksis.entity;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "notification")
@Getter
@Setter
public class Notification extends BaseEntity {
    //알림 id
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //메시지
    private String message;

    //확인 여부
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    @ColumnDefault("false") //미확인
    private Boolean isRead;

    //유형
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
}
