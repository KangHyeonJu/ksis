package com.boot.ksis.entity.IdClass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


@Getter
@Setter
public class DeviceNoticeId implements Serializable {
    //디바이스 id
    private Long deviceId;

    //공지 id
    private Long noticeId;

    // 기본 생성자, equals, hashCode, getters, setters 필요
    public DeviceNoticeId() {}

    public DeviceNoticeId(Long deviceId, Long noticeId) {
        this.deviceId = deviceId;
        this.noticeId = noticeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceNoticeId that = (DeviceNoticeId) o;
        return Objects.equals(deviceId, that.deviceId) &&
                Objects.equals(noticeId, that.noticeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, noticeId);
    }
}
