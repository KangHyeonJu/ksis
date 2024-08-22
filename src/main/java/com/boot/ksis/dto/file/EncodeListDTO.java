package com.boot.ksis.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EncodeListDTO {
    //인코딩 아이디
    private Long encodedResourceId;
    //인코딩 경로
    private String filePath;
    //제목
    private String fileTitle;
    //해상도
    private String resolution;
    //포맷
    private String format;
}
