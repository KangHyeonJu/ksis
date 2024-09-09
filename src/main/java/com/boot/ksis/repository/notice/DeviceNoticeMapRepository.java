package com.boot.ksis.repository.notice;

import com.boot.ksis.dto.pc.DeviceListDTO;
import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, Long> {

    @Query("SELECT new com.boot.ksis.dto.pc.DeviceListDTO(d.deviceId, d.deviceName) FROM Device d WHERE d.device.Id = :deviceId") //추가로직 필요없으므로 DTO에 바로 받아 넣기
    List<DeviceListDTO> findByDeviceId(@Param("deviceId") Long deviceId);
}