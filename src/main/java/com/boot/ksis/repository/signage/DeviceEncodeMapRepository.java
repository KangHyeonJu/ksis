package com.boot.ksis.repository.signage;

import com.boot.ksis.entity.IdClass.DeviceEncodeId;
import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceEncodeMapRepository extends JpaRepository<DeviceEncodeMap, DeviceEncodeId> {
    void deleteByDeviceIdIn(List<Long> deviceId);

    void deleteByEncodedResourceId(Long encodedResourceId);
}
