package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.IdClass.DeviceEncodeId;
import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceEncodeRepository extends JpaRepository<DeviceEncodeMap, DeviceEncodeId> {
    List<DeviceEncodeMap> findByDeviceId(Long deviceId);

    void deleteByDeviceIdAndEncodedResourceId(Long signageId, Long encodedResourceId);
}
