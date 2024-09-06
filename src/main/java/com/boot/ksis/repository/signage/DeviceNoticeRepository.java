package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceNoticeRepository extends JpaRepository<DeviceNoticeMap, DeviceNoticeId> {
    List<DeviceNoticeMap> findByDeviceId(Long deviceId);
    void deleteByDeviceIdIn(List<Long> deviceIds);
}
