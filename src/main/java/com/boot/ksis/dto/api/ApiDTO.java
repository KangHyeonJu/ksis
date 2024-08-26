package com.boot.ksis.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiDTO {

    //api아이디
    private Long apiId;
    //api이름
    private String apiName;
    //제공처
    private String provider;
    //키
    private String keyValue;
    //만료일
    private LocalDateTime expiryDate;
    //목적
    private String purpose;
}
