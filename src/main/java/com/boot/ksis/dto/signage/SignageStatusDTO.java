package com.boot.ksis.dto.signage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class SignageStatusDTO {
    //디바이스 id
    private Long deviceId;

    //디바이스 이름
    private String deviceName;

    //디바이스 연결 상태
    private Boolean isConnect;
}
