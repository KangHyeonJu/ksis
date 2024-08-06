package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "refreshToken")
@Getter
@Setter
public class RefreshToken {
    //계정 아이디
    @Id
    private String accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;

    //토큰값
    private String tokenValue;
}
