package com.boot.ksis.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NoticeDTO {
    //공지 아이디
    private Long noticeId;
    //디바이스 이름목록
    private List<PcListDTO> pcList;
    //제목
    private String title;
    //내용
    private String content;
    //노출 시작일
    private LocalDateTime startDate;
    //노출 종료일
    private LocalDateTime endDate;
}
