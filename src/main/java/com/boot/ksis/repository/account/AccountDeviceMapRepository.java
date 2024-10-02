package com.boot.ksis.repository.account;

import com.boot.ksis.entity.IdClass.AccountDeviceId;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountDeviceMapRepository extends JpaRepository<AccountDeviceMap, AccountDeviceId> {
    List<AccountDeviceMap> findByDeviceId(Long deviceId);

    List<AccountDeviceMap> findByAccountId(String accountId);

    void deleteByDeviceId(Long deviceId);

    void deleteByDeviceIdIn(List<Long> deviceIds);
}
