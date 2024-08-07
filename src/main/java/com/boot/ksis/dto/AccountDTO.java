package com.boot.ksis.dto;

import com.boot.ksis.constant.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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



    @Override
    public String toString() {
        return "AccountDTO{" +
                "accountId='" + accountId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", birthdate='" + birthDate + '\'' +
                ", businessTel='" + businessTel + '\'' +
                ", emergencyTel='" + emergencyTel + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }
}
