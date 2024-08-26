package com.boot.ksis.dto.signage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignageGridDTO {
    //디바이스 id
    private Long deviceId;

    //디바이스 이름
    private String deviceName;

    //재생목록 썸네일
    private String thumbNail;
}
