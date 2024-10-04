package com.boot.ksis.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class TotalFileCountDTO {
    //원본 이미지 수
    private Long countImage;

    //인코딩 이미지 수
    private Long countEncodedImage;

    //원본 영상 수
    private Long countVideo;

    //인코딩 영상 수
    private Long countEncodedVideo;
}
