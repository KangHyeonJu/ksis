package com.boot.ksis.dto.signage;

import com.boot.ksis.dto.account.AccountDeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter @Getter
@AllArgsConstructor
public class SignageNoticeDTO {
    //공지 아이디
    private Long noticeId;

    //제목
    private String title;

    //계정
    private AccountDeviceDTO account;

    //등록일
    private LocalDateTime regDate;

    //노출 시작일
    private LocalDate startDate;

    //노출 종료일
    private LocalDate endDate;
}
