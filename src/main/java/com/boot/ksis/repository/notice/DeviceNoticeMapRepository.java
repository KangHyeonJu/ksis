package com.boot.ksis.repository.notice;

import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, DeviceNoticeId> {
}