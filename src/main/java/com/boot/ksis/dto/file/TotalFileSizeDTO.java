package com.boot.ksis.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TotalFileSizeDTO {
    //원본 이미지 total
    private Long totalImageSize;

    //원본 영상 total
    private Long totalVideoSize;

    //인코딩 이미지 total
    private Long totalEncodedImageSize;

    //인코딩 영상 total
    private Long totalEncodedVideoSize;
}
