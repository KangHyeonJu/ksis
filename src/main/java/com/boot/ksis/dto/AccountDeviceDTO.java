package com.boot.ksis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDeviceDTO {
    //계정 아이디
    private String accountId;

    //이름
    private String name;
}
