package com.boot.ksis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignageResourceDTO {
    //리소스 아이디
    private Long encodedResourceId;

    //리소스 제목
    private String fileTitle;

    //리소스 썸네일 경로
    private String thumbFilePath;
}
