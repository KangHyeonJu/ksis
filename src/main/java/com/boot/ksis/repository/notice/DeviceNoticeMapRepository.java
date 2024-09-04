package com.boot.ksis.repository.notice;

import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, DeviceNoticeId> {

    void deleteByDeviceIdIn(List<Long> deviceIds);

}