package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account_device_map")
@IdClass(AccountDeviceId.class)
@Getter
@Setter
public class AccountDeviceMap {
    //계정 아이디
    @Id
    private String accountId;

    //디바이스 id
    @Id
    private Long deviceId;

    @ManyToOne
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @MapsId("deviceId")
    @JoinColumn(name = "device_id")
    private Device device;
}
