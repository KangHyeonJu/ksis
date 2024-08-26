package com.boot.ksis.dto.account;

import com.boot.ksis.constant.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountDTO {
    private String accountId;
    private String password;
    private String name;
    private String birthDate;
    private String businessTel;
    private String emergencyTel;
    private String email;
    private String position;
    private Gender gender;
}
