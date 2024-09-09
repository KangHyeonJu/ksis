package com.boot.ksis.dto.account;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Builder
public class AccountNameDTO {
    private String accountId;
    private String name;
}
