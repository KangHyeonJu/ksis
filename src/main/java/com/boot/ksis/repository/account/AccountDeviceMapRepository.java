package com.boot.ksis.repository.account;

import com.boot.ksis.entity.IdClass.AccountDeviceId;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountDeviceMapRepository extends JpaRepository<AccountDeviceMap, AccountDeviceId> {
    List<AccountDeviceMap> findByDeviceId(Long deviceId);

    void deleteByDeviceId(Long deviceId);

    void deleteByDeviceIdIn(List<Long> deviceIds);

    // 사용자가 담당하는 디바이스 ID 목록을 가져오는 메소드
    @Query("SELECT adm.deviceId FROM AccountDeviceMap adm WHERE adm.accountId = :accountId")
    List<Long> findDeviceIdsByAccountId(String accountId);

    // 특정 사용자가 특정 디바이스를 담당하는지 확인하는 메소드
    boolean existsByAccountIdAndDeviceId(String accountId, Long deviceId);
}
