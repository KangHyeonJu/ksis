package com.boot.ksis.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
public class AccountDeviceDTO {
    //계정 아이디
    private String accountId;

    //이름
    private String name;
}