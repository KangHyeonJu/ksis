package com.boot.ksis.repository.notice;

import com.boot.ksis.constant.Role;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.MapsId.DeviceNoticeMap;
import com.boot.ksis.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceNoticeMapRepository extends JpaRepository<DeviceNoticeMap, Long> {

    // 특정 공지에 해당하는 디바이스 목록을 조회하는 쿼리
    @Query("SELECT d FROM DeviceNoticeMap d WHERE d.notice.noticeId = :noticeId")
    List<DeviceNoticeMap> findByNoticeId(@Param("noticeId") Long noticeId);


    // 특정 공지에 해당하는 매핑 정보를 삭제하는 메소드
    void deleteByNoticeId(Long noticeId);


    //유저
    Page<DeviceNoticeMap> findByDevice_DeviceNameContainingIgnoreCaseAndNotice_AccountAndNotice_IsActive(String deviceName,
                                                                                                         Account accountId, boolean isActive, Pageable pageable);
    Page<DeviceNoticeMap> findByDevice_DeviceNameContainingIgnoreCaseAndNotice_Account_RoleAndNotice_IsActive(String deviceName, Role role, boolean isActive, Pageable pageable);


    //관리자 재생장치로 검색
    @Query("SELECT dnm FROM DeviceNoticeMap dnm " +
            "JOIN dnm.notice n " +
            "WHERE n.isActive = true " +
            "AND dnm.device.deviceName LIKE CONCAT('%', :searchTerm, '%') " +  // CONCAT 사용으로 가독성 향상
            "ORDER BY n.account.role ASC, n.regTime DESC")
    Page<DeviceNoticeMap> findActiveNoticesWithDevice(@Param("searchTerm") String searchTerm, Pageable pageable);

    //user 재생장치로 검색
    @Query("SELECT dnm FROM DeviceNoticeMap dnm " +
            "JOIN dnm.notice n " +
            "JOIN n.account a " +
            "WHERE (a.role = 'admin' OR (a.role = 'user' AND a.accountId = :accountId)) " +
            "AND n.isActive = true " +
            "AND dnm.device.deviceName LIKE CONCAT('%', :searchTerm, '%') " +
            "ORDER BY a.role ASC, n.regTime DESC")
    Page<DeviceNoticeMap> findUserNoticesByRoleWithDevice(
            @Param("accountId") String accountId,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );




}