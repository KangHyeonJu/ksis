package com.boot.ksis.repository.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SignageRepository extends JpaRepository<Device, Long> {
    List<Device> findByDeviceTypeOrderByRegTimeDesc(DeviceType deviceType);

    Device findByDeviceId(Long deviceId);

    Device findByMacAddress(String macAddress);

    Optional<Device> findBySignageKey(String key);

    @Query("SELECT d FROM Device d " +
            "JOIN AccountDeviceMap adm ON d.deviceId = adm.device.deviceId " +
            "WHERE adm.account.accountId = :accountId AND d.deviceType = :deviceType " +
            "ORDER BY d.regTime DESC")
    List<Device> findDevicesByAccountIdAndType(@Param("accountId") String accountId,
                                               @Param("deviceType") DeviceType deviceType);
}
