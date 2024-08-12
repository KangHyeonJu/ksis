package com.boot.ksis.service.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.AccountListDTO;
import com.boot.ksis.dto.PcFormDTO;
import com.boot.ksis.dto.PcListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.pc.PcRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PcService {
    private final PcRepository pcRepository;
    private final AccountRepository accountRepository;
    private final AccountDeviceMapRepository accountDeviceMapRepository;
    public List<PcListDTO> getPcList(){
        List<Device> devices = pcRepository.findByDeviceType(DeviceType.PC);

        return devices.stream().map(device -> {
            List<AccountListDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountListDTO(account.getAccountId(), account.getName());
                    })
                    .collect(Collectors.toList());

            return new PcListDTO(device.getDeviceId(), device.getDeviceName(), accountDTOList, device.getRegTime());
        }).collect(Collectors.toList());
    }

    public void saveNewPc(PcFormDTO pcFormDto, List<String> accountList){
        Device device = pcFormDto.createNewPc();
        pcRepository.save(device);

        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(pcFormDto.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }
    }

    public void updatePc(PcFormDTO pcFormDto, List<String> accountList){
        Device device = pcRepository.findById(pcFormDto.getDeviceId()).orElseThrow(EntityNotFoundException::new);
        device.updatePc(pcFormDto);

        accountDeviceMapRepository.deleteByDeviceId(device.getDeviceId());

        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(pcFormDto.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }

        pcRepository.save(device);
    }

    @Transactional
    public PcFormDTO getPcDtl(Long pcId){
        Device device = pcRepository.findById(pcId).orElseThrow(EntityNotFoundException::new);

        List<AccountListDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                .stream()
                .map(map -> {
                    Account account = map.getAccount();
                    return new AccountListDTO(account.getAccountId(), account.getName());
                })
                .collect(Collectors.toList());

        return PcFormDTO.of(device, accountDTOList);
    }
}
