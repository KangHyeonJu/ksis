package com.boot.ksis.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OriginResourceListDTO {
    //원본 아이디
    private Long originalResourceId;
    //작성자
    private String accountId;
    //파일 이름
    private String fileName;
    //경로
    private String filePath;
    //제목
    private String fileTitle;
    //해상도
    private String resolution;
    //포맷
    private String format;
    //파일 사이즈
    private int fileSize;
    //등록일
    private LocalDateTime regTime;
}

