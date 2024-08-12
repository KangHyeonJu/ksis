package com.boot.ksis.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeFormDTO {
    //공지 아이디
    private Long noticeId;
    // 작성자를 식별하기 위한 필드
    private Long accountId;
    //디바이스 아이디
    private Long deviceId;
    //디바이스명
    private String deviceName;
    //등록일
    private LocalDateTime createTime;
    //제목
    private String title;
    //내용
    private String content;
    //노출 시작일
    private LocalDateTime startDate;
    //노출 종료일
    private LocalDateTime endDate;
}
