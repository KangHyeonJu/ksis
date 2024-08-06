package com.boot.ksis.entity;

import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@Setter
public class Notice extends BaseEntity {
    //공지 아이디
    @Id
    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //제목
    @Column(nullable = false, length = 50)
    private String title;

    //내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //노출 시작일
    private LocalDateTime startDate;

    //노출 종료일
    private LocalDateTime endDate;
}
