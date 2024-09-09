package com.boot.ksis.dto.log;

import com.boot.ksis.dto.account.AccountNameDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter @Getter
@Builder
public class LogDTO {
    //로그아이디
    private Long logId;

    //계정아이디
    private AccountNameDTO account;

    //내용
    private String detail;

    //접근일시
    private LocalDateTime dateTime;
}
