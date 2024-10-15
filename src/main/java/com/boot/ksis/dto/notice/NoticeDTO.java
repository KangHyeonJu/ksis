package com.boot.ksis.dto.notice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

public class NoticeDTO {
    // 공지 아이디
    private Long noticeId;

    // 계정 아이디
    private String accountId;

    //계정 이름
    private String name;

    // 재생장치 아이디
    private List<Long> deviceIds;

    // 제목
    private String title;

    // 내용
    private String content;

    // 노출 시작일
    private LocalDate startDate;

    // 노출 종료일
    private LocalDate endDate;

    //등록시간
    private LocalDateTime regTime;
    //수정 시간
    private LocalDateTime updateTime;
    
    //활성화 여부
    private boolean isActive; 
}
