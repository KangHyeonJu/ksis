package com.boot.ksis.dto;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.entity.Device;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
public class SignageFormDTO {
    private Long deviceId;

    //mac 주소
    private String macAddress;

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
    private List<AccountListDTO> accountList;

    private static ModelMapper modelMapper = new ModelMapper();

    public Device createNewSignage(){
        return modelMapper.map(this, Device.class);
    }
}
