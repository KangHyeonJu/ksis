package com.boot.ksis.repository.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SignageRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceType(DeviceType deviceType);
}
