package com.boot.ksis.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NewNoticeDTO {

        //공지 id
        private Long noticeId;

        //작성자 id
        private String accountId;

        //작성자 이름
        private String name;

        //제목
        private String title;

        //내용
        private String content;

        //디바이스 리스트
        private List<DeviceNoticeDTO> deviceList;

        //작성일
        private LocalDateTime regDate;

        // 노출 시작일
        private LocalDate startDate;

        // 노출 종료일
        private LocalDate endDate;

}
