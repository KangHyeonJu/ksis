package com.boot.ksis.repository.notice;

import com.boot.ksis.dto.pc.DeviceListDTO;
import com.boot.ksis.entity.IdClass.DeviceNoticeId;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, Long> {

    //디바이스 아이디로 디바이스이름 device엔티티에서 찾아서 리스트 형태로 가져오기
    @Query("SELECT d.deviceName FROM Device d WHERE d.deviceId = :deviceId")
    List<String> findDeviceNamesByDeviceId(String deviceId);
}