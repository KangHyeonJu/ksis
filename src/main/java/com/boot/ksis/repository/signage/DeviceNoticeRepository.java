package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceNoticeRepository extends JpaRepository<DeviceNoticeMap, DeviceNoticeId> {
    List<DeviceNoticeMap> findByDeviceId(Long deviceId);
    void deleteByDeviceIdIn(List<Long> deviceIds);

    Page<DeviceNoticeMap> findByDeviceId(Long deviceId, Pageable pageable);
    Page<DeviceNoticeMap> findByDeviceIdAndNotice_TitleContainingIgnoreCase(Long deviceId, String title, Pageable pageable);

    // DeviceNoticeMapRepository
    Page<DeviceNoticeMap> findByDeviceIdAndNoticeIn(Long signageId, List<Notice> noticeList, Pageable pageable);

}
