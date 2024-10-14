package com.boot.ksis.repository.account;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.IdClass.AccountDeviceId;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountDeviceMapRepository extends JpaRepository<AccountDeviceMap, AccountDeviceId> {
    List<AccountDeviceMap> findByDeviceId(Long deviceId);

    List<AccountDeviceMap> findByAccountId(String accountId);

    void deleteByDeviceId(Long deviceId);

    void deleteByDeviceIdIn(List<Long> deviceIds);

    @Query("SELECT adm FROM AccountDeviceMap adm WHERE (adm.accountId LIKE %:searchTerm% OR adm.account.name LIKE %:searchTerm%) AND adm.device.deviceType = :deviceType")
    List<AccountDeviceMap> searchByAccountIdOrName(@Param("searchTerm") String searchTerm, @Param("deviceType") DeviceType deviceType);

}
