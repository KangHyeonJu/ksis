//package com.boot.ksis.service.signage;
//
//import com.boot.ksis.constant.DeviceType;
//import com.boot.ksis.entity.Device;
//import com.boot.ksis.repository.signage.SignageRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//@SpringBootTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//class SignageServiceTest {
//    @Autowired
//    SignageRepository signageRepository;
//    public Device addDevice(){
//        Device device = new Device();
//
//        device.setMacAddress("11:11:11:11:11:11");
//        device.setDeviceName("test 재생장치");
//        device.setLocation("test 주소");
//        device.setDetailAddress("test 상세주소");
//        device.setDeviceType(DeviceType.SIGNAGE);
//        device.setIsShow(true);
//
//        return signageRepository.save(device);
//    }
//
//    @Test
//    @DisplayName("재생목록 등록 테스트")
//    public void postPlaylistTest(){
//        Device device = addDevice();
//
//
//    }
//}
