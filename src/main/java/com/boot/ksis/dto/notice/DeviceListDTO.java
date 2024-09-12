package com.boot.ksis.dto.notice;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DeviceListDTO {
    //공지 id
    private Long noticeId;

    //작성자 id
    private String accountId;

    //작성자 이름
    private String name;

    //제목
    private String title;

    //디바이스 리스트
    private List<DeviceNoticeDTO> deviceList;

    //작성일
    private LocalDateTime regDate;

}
