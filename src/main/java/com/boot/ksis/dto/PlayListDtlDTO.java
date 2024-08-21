package com.boot.ksis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayListDtlDTO {
    //인코딩 리소스 아이디
    private Long encodedResourceId;

    //인코딩 리소스 제목
    private String fileTitle;

    //썸네일 경로
    private String thumbFilePath;

    //순서
    private int sequence;
}
