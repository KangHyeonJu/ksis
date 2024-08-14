package com.boot.ksis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountListDTO {
    private String accountId;
    private String name;
    private String businessTel;
    private Boolean isActive;
}
