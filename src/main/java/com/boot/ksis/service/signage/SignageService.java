package com.boot.ksis.service.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.AccountListDTO;
import com.boot.ksis.dto.DeviceListDTO;
import com.boot.ksis.dto.SignageFormDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.signage.SignageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SignageService {
    private final SignageRepository signageRepository;
    private final AccountRepository accountRepository;
    private final AccountDeviceMapRepository accountDeviceMapRepository;

    public List<DeviceListDTO> getSignageList(){
        List<Device> deviceList = signageRepository.findByDeviceType(DeviceType.SIGNAGE);

        return deviceList.stream().map(device -> {
            List<AccountListDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountListDTO(account.getAccountId(), account.getName());
                    })
                    .collect(Collectors.toList());

            return new DeviceListDTO(device.getDeviceId(), device.getDeviceName(), accountDTOList, device.getRegTime());
        }).collect(Collectors.toList());
    }

    public void saveNewSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageFormDTO.createNewSignage();
        device.setIsShow(false);

        signageRepository.save(device);

        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(signageFormDTO.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }
    }

}
