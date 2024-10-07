package com.boot.ksis.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ResourceListDTO {
    //원본 아이디
    private Long originalResourceId;
    //썸네일
    private String thumbFilePath;
    //경로
    private String filePath;
    //제목
    private String fileTitle;
    //해상도
    private String resolution;
    //포맷
    private String format;
    //등록일
    private LocalDateTime regTime;
}
