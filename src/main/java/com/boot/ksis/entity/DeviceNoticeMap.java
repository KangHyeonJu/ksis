package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "device_notice_map")
@IdClass(DeviceNoticeId.class)
@Getter
@Setter
public class DeviceNoticeMap {
    //디바이스 id
    @Id
    private Long deviceId;

    //공지 id
    @Id
    private Long noticeId;

    @ManyToOne
    @MapsId("deviceId")
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne
    @MapsId("noticeId")
    @JoinColumn(name = "notice_id")
    private Notice notice;
}
