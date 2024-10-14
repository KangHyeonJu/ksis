package com.boot.ksis.service.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.account.AccountDeviceDTO;
import com.boot.ksis.dto.pc.DeviceListDTO;
import com.boot.ksis.dto.pc.PcFormDTO;
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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public Page<DeviceListDTO> getPcList(String accountId, int page, int size, String searchTerm, String searchCategory){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        Page<Device> deviceList;

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("deviceName")){
                deviceList = pcRepository.findDevicesByAccountIdAndDeviceTypeAndDeviceName(accountId, DeviceType.PC, searchTerm, pageable);
            }else {
                deviceList = pcRepository.findDevicesByAccountIdAndDeviceType(accountId, DeviceType.PC, pageable);
            }
        }else {
            deviceList = pcRepository.findDevicesByAccountIdAndDeviceType(accountId, DeviceType.PC, pageable);
        }
        List<DeviceListDTO> deviceListDTOList = new ArrayList<>();

        for(Device device : deviceList){
            List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountDeviceDTO(account.getAccountId(), account.getName());
                    })
                    .toList();

            DeviceListDTO deviceListDTO = DeviceListDTO.builder()
                    .deviceId(device.getDeviceId())
                    .accountList(accountDTOList)
                    .deviceName(device.getDeviceName())
                    .regDate(device.getRegTime())
                    .build();

            deviceListDTOList.add(deviceListDTO);
        }

        return new PageImpl<>(deviceListDTOList, pageable, deviceList.getTotalElements());
    }

    public Page<DeviceListDTO> getPcAll(int page, int size, String searchTerm, String searchCategory){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        Page<Device> deviceList;

        if(searchCategory != null && !searchTerm.isEmpty()){
            if(searchCategory.equals("deviceName")){
                deviceList = pcRepository.findByDeviceTypeAndDeviceNameContainingIgnoreCase(DeviceType.PC, searchTerm, pageable);
            }else if(searchCategory.equals("account")){
                // accountId 또는 name 에서 검색
                List<AccountDeviceMap> accountDeviceMaps = accountDeviceMapRepository.searchByAccountIdOrName(searchTerm, DeviceType.PC);

                List<Long> deviceIds = accountDeviceMaps.stream()
                        .map(map -> map.getDevice().getDeviceId())
                        .collect(Collectors.toList());

                deviceList = pcRepository.findByDeviceIdIn(deviceIds, pageable);
            }else {
                deviceList = pcRepository.findByDeviceType(DeviceType.PC, pageable);
            }
        }else {
            deviceList = pcRepository.findByDeviceType(DeviceType.PC, pageable);
        }
        List<DeviceListDTO> deviceListDTOList = new ArrayList<>();

        for(Device device : deviceList){
            List<AccountDeviceDTO> accountDTOList = accountDeviceMapRepository.findByDeviceId(device.getDeviceId())
                    .stream()
                    .map(map -> {
                        Account account = map.getAccount();
                        return new AccountDeviceDTO(account.getAccountId(), account.getName());
                    })
                    .toList();

            DeviceListDTO deviceListDTO = DeviceListDTO.builder()
                                                        .deviceId(device.getDeviceId())
                                                        .accountList(accountDTOList)
                                                        .deviceName(device.getDeviceName())
                                                        .regDate(device.getRegTime())
                                                        .build();

            deviceListDTOList.add(deviceListDTO);
        }

        return new PageImpl<>(deviceListDTOList, pageable, deviceList.getTotalElements());
    }

    public void saveNewPc(PcFormDTO pcFormDto, List<String> accountList){
        Device device = pcFormDto.createNewPc();
        pcRepository.save(device);

        for (String accountId : accountList) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

            AccountDeviceMap accountDeviceMap = new AccountDeviceMap();
            accountDeviceMap.setDeviceId(pcFormDto.getDeviceId());
            accountDeviceMap.setAccountId(accountId);

            accountDeviceMap.setAccount(account);
            accountDeviceMap.setDevice(device);

            accountDeviceMapRepository.save(accountDeviceMap);
        }
    }

    public boolean checkMacAddress(PcFormDTO pcFormDTO){
        Device device = pcRepository.findByMacAddress(pcFormDTO.getMacAddress());

        return device == null;
    }

    public boolean checkUpdateMacAddress(PcFormDTO pcFormDTO){
        Device device = pcRepository.findById(pcFormDTO.getDeviceId()).orElseThrow();

        Device checkDevice = pcRepository.findByMacAddress(pcFormDTO.getMacAddress());

        if(checkDevice == null){
            return true;
        }else return Objects.equals(device.getMacAddress(), pcFormDTO.getMacAddress());
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
