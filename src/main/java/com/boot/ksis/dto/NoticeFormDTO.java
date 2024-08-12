package com.boot.ksis.dto;

import com.boot.ksis.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;

@Getter
@Setter
public class NoticeFormDTO {
    //공지 아이디
    private Long noticeId;
    // 작성자를 식별하기 위한 필드
    private String accountId;
    //디바이스 아이디
    private Long deviceId;
    //디바이스명
    private String deviceName;
    //등록일
    private LocalDateTime createTime;
    //제목
    private String title;
    //내용
    private String content;
    //노출 시작일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    //노출 종료일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private static ModelMapper modelMapper = new ModelMapper();

    public Notice createNewNotice(){
        return modelMapper.map(this, Notice.class);
    }
}
