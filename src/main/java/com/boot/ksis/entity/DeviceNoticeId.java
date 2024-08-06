package com.boot.ksis.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class DeviceNoticeId implements Serializable {
    //디바이스 id
    private Long deviceId;

    //공지 id
    private Long noticeId;
}
