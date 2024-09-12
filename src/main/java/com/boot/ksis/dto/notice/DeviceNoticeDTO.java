package com.boot.ksis.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeviceNoticeDTO {
    //디바이스 아이디
    private Long deviceId;

    //디바이스 이름
    private String deviceName;
}
