package com.boot.ksis.dto.signage;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.account.AccountDeviceDTO;
import com.boot.ksis.entity.Device;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SignageFormDTO {
    private Long deviceId;

    //ip 주소
    private String ipAddress;

    //key
    private String deviceKey;

    //디바이스명
    private String deviceName;

    //위치
    private String location;

    //상세주소
    private String detailAddress;

    //해상도
    private String resolution;

    //크기
    private String screenSize;

    //디바이스 유형
    private DeviceType deviceType;

    //담당자 리스트
    private List<AccountDeviceDTO> accountList;

    //등록일
    private LocalDateTime regTime;

    //공지표시여부
    private Boolean isShow;

    private static ModelMapper modelMapper = new ModelMapper();

    public Device createNewSignage(){
        return modelMapper.map(this, Device.class);
    }

    public static SignageFormDTO of(Device device, List<AccountDeviceDTO> accountListDTOList){
        SignageFormDTO signageFormDTO = modelMapper.map(device, SignageFormDTO.class);
        signageFormDTO.setAccountList(accountListDTOList);

        return signageFormDTO;
    }
}
