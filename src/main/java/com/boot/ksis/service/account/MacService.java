package com.boot.ksis.service.account;

import com.boot.ksis.entity.Device;
import com.boot.ksis.repository.signage.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class MacService {

    @Autowired
    private DeviceRepository deviceRepository;

    public Map<String, Object> verifyMacAddress(String macAddress) {
        Optional<Device> device = deviceRepository.findByMacAddress(macAddress);

        if (device.isPresent()) {
            return Map.of("success", true, "message", "MAC 주소가 인증되었습니다.");
        } else {
            return Map.of("success", false, "message", "허용되지 않은 MAC 주소입니다.");
        }
    }
}
