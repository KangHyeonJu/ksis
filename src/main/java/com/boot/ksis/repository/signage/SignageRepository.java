package com.boot.ksis.repository.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SignageRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceType(DeviceType deviceType);

    Device findByDeviceId(Long deviceId);

    Device findByMacAddress(String macAddress);

    Optional<Device> findBySignageKey(String key);
}
