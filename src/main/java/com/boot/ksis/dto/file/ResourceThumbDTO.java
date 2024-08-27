package com.boot.ksis.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResourceThumbDTO {
    //원본 아이디
    private Long originalResourceId;

    //리소스 아이디
    private Long encodedResourceId;

    //리소스 제목
    private String fileTitle;

    //리소스 썸네일 경로
    private String thumbFilePath;
}
