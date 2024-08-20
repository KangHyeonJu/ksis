package com.boot.ksis;

import com.boot.ksis.entity.Device;
import com.boot.ksis.repository.DeviceRepository;
import com.boot.ksis.service.MacService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class KsisApplicationTests {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private MacService macService;  // MacService를 주입받음

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void TC_DEVICE_AUTH01() {
        String macAddress = "00:1A:2B:3C:4D:5E";
        Device device = new Device();
        device.setMacAddress(macAddress);
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.of(device));

        Map<String, Object> response = macService.verifyMacAddress(macAddress);

        assertThat(response.get("success")).isEqualTo(true);
        assertThat(response.get("message")).isEqualTo("MAC 주소가 인증되었습니다.");
    }

    @Test
    void TC_DEVICE_AUTH02() {
        String macAddress = "AA:BB:CC:DD:EE:FF";
        when(deviceRepository.findByMacAddress(macAddress)).thenReturn(Optional.empty());

        Map<String, Object> response = macService.verifyMacAddress(macAddress);

        assertThat(response.get("success")).isEqualTo(false);
        assertThat(response.get("message")).isEqualTo("허용되지 않은 MAC 주소입니다.");
    }
}
