package com.boot.ksis.dto.account;

import lombok.*;

@Setter @Getter
@Builder
public class AccountNameDTO {
    private String accountId;
    private String name;
}
