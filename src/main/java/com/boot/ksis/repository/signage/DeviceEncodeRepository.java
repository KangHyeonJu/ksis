package com.boot.ksis.repository.signage;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.IdClass.DeviceEncodeId;
import com.boot.ksis.entity.MapsId.DeviceEncodeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceEncodeRepository extends JpaRepository<DeviceEncodeMap, DeviceEncodeId> {
    Page<DeviceEncodeMap> findByDeviceId(Long deviceId, Pageable pageable);

    void deleteByDeviceIdAndEncodedResourceId(Long signageId, Long encodedResourceId);

    Page<DeviceEncodeMap> findByDeviceIdAndEncodedResource_FileTitleContainingIgnoreCaseAndEncodedResource_ResourceType(Long signageId, String fileTitle, ResourceType resourceType, Pageable pageable);

    Page<DeviceEncodeMap> findByDeviceIdAndEncodedResource_ResourceType(Long signageId, ResourceType resourceType, Pageable pageable);

    Page<DeviceEncodeMap> findByDeviceIdAndEncodedResource_FileTitleContainingIgnoreCase(Long signageId, String fileTitle, Pageable pageable);

    List<DeviceEncodeMap> findByDeviceId(Long signageId);
}
