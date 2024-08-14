package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceNoticeRepository extends JpaRepository<DeviceNoticeMap, DeviceNoticeMap> {
    List<DeviceNoticeMap> findByDeviceId(Long deviceId);
}
