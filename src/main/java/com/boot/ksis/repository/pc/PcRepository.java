package com.boot.ksis.repository.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PcRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceType(DeviceType deviceType);
}
