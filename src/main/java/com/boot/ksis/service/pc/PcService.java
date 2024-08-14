package com.boot.ksis.service.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.AccountDeviceDTO;
import com.boot.ksis.dto.PcFormDTO;
import com.boot.ksis.dto.DeviceListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.MapsId.AccountDeviceMap;
import com.boot.ksis.repository.account.AccountDeviceMapRepository;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.repository.pc.PcRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    public List<DeviceListDTO> getPcList(){
        List<Device> devices = pcRepository.findByDeviceType(DeviceType.PC);

        return devices.stream().map(device -> {
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

        List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                .stream()
                .map(map -> {
                    Account account = map.getAccount();
                    return new AccountDeviceDTO(account.getAccountId(), account.getName());
                })
                .collect(Collectors.toList());

        return PcFormDTO.of(device, accountDTOList);
    }

    //pc 삭제
    @Transactional
    public void deletePcs(List<Long> pcIds){
        accountDeviceMapRepository.deleteByDeviceIdIn(pcIds);

        entityManager.flush();

        pcRepository.deleteAllByIdInBatch(pcIds);
    }
}
