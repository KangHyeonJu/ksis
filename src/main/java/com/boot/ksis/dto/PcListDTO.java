package com.boot.ksis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PcListDTO {
    //디바이스 id
    private Long deviceId;

    //디바이스 이름
    private String deviceName;

    //담당자 리스트
    private List<AccountListDTO> accountList;

    //등록일
    private LocalDateTime regDate;
}
