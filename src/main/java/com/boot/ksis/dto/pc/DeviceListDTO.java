package com.boot.ksis.dto.pc;

import com.boot.ksis.dto.account.AccountDeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DeviceListDTO {
    //디바이스 id
    private Long deviceId;

    //디바이스 이름
    private String deviceName;

    //담당자 리스트
    private List<AccountDeviceDTO> accountList;

    //등록일
    private LocalDateTime regDate;
}
