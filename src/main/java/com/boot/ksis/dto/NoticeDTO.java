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
    //담당자 리스트
    private List<AccountListDTO> accountList;
    //디바이스 이름목록
    private List<PcListDTO> pcList;
    //제목
    private String title;
    //내용
    private String content;

}
