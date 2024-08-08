package com.boot.ksis.service.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import com.boot.ksis.repository.pc.PcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PcService {
    private final PcRepository pcRepository;
    public List<Device> getPcList(){
        return pcRepository.findByDeviceType(DeviceType.PC);
    }
}
