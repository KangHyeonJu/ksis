package com.boot.ksis.dto.signage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignageResourceDTO {
    //리소스 아이디
    private Long encodedResourceId;

    //리소스 제목
    private String fileTitle;

    //리소스 썸네일 경로
    private String thumbFilePath;
}
