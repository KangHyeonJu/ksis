package com.boot.ksis.repository.notice;

import com.boot.ksis.constant.Role;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, Long> {

    // 특정 공지에 해당하는 디바이스 목록을 조회하는 쿼리
    @Query("SELECT d FROM DeviceNoticeMap d WHERE d.notice.noticeId = :noticeId")
    List<DeviceNoticeMap> findByNoticeId(@Param("noticeId") Long noticeId);

    // 특정 공지에 해당하는 디바이스 목록을 조회하는 쿼리
    @Query("SELECT d FROM DeviceNoticeMap d WHERE d.device.deviceId = :deviceId")
    List<DeviceNoticeMap> findByDeviceId(@Param("deviceId") Long deviceId);

    // 특정 공지에 해당하는 매핑 정보를 삭제하는 메소드
    void deleteByNoticeId(Long noticeId);

    //관리자
    Page<DeviceNoticeMap> findByDevice_DeviceNameContainingIgnoreCaseAndNotice_Active(String deviceName, boolean isActive, Pageable pageable);


    //유저
    Page<DeviceNoticeMap> findByDevice_DeviceNameContainingIgnoreCaseAndNotice_AccountAndNotice_IsActive(String deviceName, Account accountId, boolean isActive, Pageable pageable);
    Page<DeviceNoticeMap> findByDevice_DeviceNameContainingIgnoreCaseAndNotice_Account_RoleAndNotice_IsActive(String deviceName, Role role, boolean isActive, Pageable pageable);

}