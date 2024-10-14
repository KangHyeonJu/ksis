package com.boot.ksis.repository.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PcRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeOrderByRegTimeDesc(DeviceType deviceType);

    Page<Device> findByDeviceType(DeviceType deviceType, Pageable pageable);

    Page<Device> findByDeviceTypeAndDeviceNameContainingIgnoreCase(DeviceType deviceType, String deviceName, Pageable pageable);

    Page<Device> findByDeviceIdIn(List<Long> deviceIds, Pageable pageable);

    Device findByMacAddress(String macAddress);

    @Query("SELECT d FROM Device d " +
            "JOIN AccountDeviceMap adm ON d.deviceId = adm.device.deviceId " +
            "WHERE adm.account.accountId = :accountId AND d.deviceType = :deviceType " +
            "ORDER BY d.regTime DESC")
    Page<Device> findDevicesByAccountIdAndDeviceType(@Param("accountId") String accountId, @Param("deviceType") DeviceType deviceType, Pageable pageable);

    @Query("SELECT d FROM Device d " +
            "JOIN AccountDeviceMap adm ON d.deviceId = adm.device.deviceId " +
            "WHERE adm.account.accountId = :accountId AND d.deviceType = :deviceType " +
            "AND d.deviceName LIKE %:searchTerm% " +
            "ORDER BY d.regTime DESC")
    Page<Device> findDevicesByAccountIdAndDeviceTypeAndDeviceName(@Param("accountId") String accountId, @Param("deviceType") DeviceType deviceType, @Param("searchTerm") String searchTerm, Pageable pageable);

}
