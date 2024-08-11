package com.boot.ksis.dto;

import java.time.LocalDateTime;

public class NoticeDTO {
    private Long noticeId;
    private String accountId; // accountId를 추가
    private String title;
    private String content;
    private LocalDateTime startDate; // LocalDateTime을 문자열로 변환
    private LocalDateTime endDate; // LocalDateTime을 문자열로 변환

    // Getters and Setters
    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CharSequence getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public CharSequence getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
