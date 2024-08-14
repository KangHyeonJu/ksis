package com.boot.ksis.service.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.AccountDeviceDTO;
import com.boot.ksis.dto.DeviceListDTO;
import com.boot.ksis.dto.SignageFormDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.signage.SignageRepository;
import jakarta.persistence.EntityNotFoundException;
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
            List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountDeviceDTO(account.getAccountId(), account.getName());
                    })
                    .collect(Collectors.toList());

            return new DeviceListDTO(device.getDeviceId(), device.getDeviceName(), accountDTOList, device.getRegTime());
        }).collect(Collectors.toList());
    }

    public void saveNewSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageFormDTO.createNewSignage();

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

    public void updateSignage(SignageFormDTO signageFormDTO, List<String> accountList){
        Device device = signageRepository.findById(signageFormDTO.getDeviceId()).orElseThrow(EntityNotFoundException::new);
        device.updateSignage(signageFormDTO);

        accountDeviceMapRepository.deleteByDeviceId(device.getDeviceId());

        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(signageFormDTO.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }

        signageRepository.save(device);
    }

    @Transactional
    public SignageFormDTO getSignageDtl(Long signageId){
        Device device = signageRepository.findById(signageId).orElseThrow(EntityNotFoundException::new);

        List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                .stream()
                .map(map -> {
                    Account account = map.getAccount();
                    return new AccountDeviceDTO(account.getAccountId(), account.getName());
                })
                .collect(Collectors.toList());

        return SignageFormDTO.of(device, accountDTOList);
    }

    @Transactional
    public void updateSignageStatus(Long signageId, boolean isShow) {
        Device device = signageRepository.findById(signageId)
                .orElseThrow(() -> new RuntimeException("Signage not found"));
        device.setIsShow(isShow);
        signageRepository.save(device);
    }
}
