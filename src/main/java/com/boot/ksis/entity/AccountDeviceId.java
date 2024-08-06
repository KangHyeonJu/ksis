package com.boot.ksis.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class AccountDeviceId implements Serializable {
    //계정 아이디
    private String accountId;

    //디바이스 id
    private Long deviceId;
}
