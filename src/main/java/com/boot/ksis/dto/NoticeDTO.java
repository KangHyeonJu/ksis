package com.boot.ksis.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NoticeDTO {
    private Long noticeId;
    //담당자 리스트
    private List<AccountListDTO> accountList;
    //디바이스 이름목록
    private List<PcListDTO> pcList;
    //제목
    private String title;
    //내용
    private String content;

    //노출 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    //노출 종료일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;


    public NoticeDTO(Long noticeId, String title, String content, LocalDateTime startDate, LocalDateTime endDate) {
        this.noticeId = noticeId;
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
