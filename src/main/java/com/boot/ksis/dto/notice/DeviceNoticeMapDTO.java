package com.boot.ksis.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeviceNoticeMapDTO {

    //디바이스 id
    private Long deviceId;

    //공지 id
    private Long noticeId;
}
