package com.boot.ksis.dto.pc;

import com.boot.ksis.constant.DeviceType;
import com.boot.ksis.dto.account.AccountDeviceDTO;
import com.boot.ksis.entity.Device;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter @Setter
public class PcFormDTO {
    private Long deviceId;

    //mac 주소
    private String macAddress;

    //디바이스명
    private String deviceName;

    //위치
    private String location;

    //상세주소
    private String detailAddress;

    //디바이스 유형
    private DeviceType deviceType;

    //담당자 리스트
    private List<AccountDeviceDTO> accountList;

    private static ModelMapper modelMapper = new ModelMapper();

    public Device createNewPc(){
        return modelMapper.map(this, Device.class);
    }

    public static PcFormDTO of(Device device, List<AccountDeviceDTO> accountDTOList){
        PcFormDTO pcFormDTO = modelMapper.map(device, PcFormDTO.class);
        pcFormDTO.setAccountList(accountDTOList);
        return pcFormDTO;
    }
}
