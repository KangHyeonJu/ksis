package com.boot.ksis.dto.file;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TotalFileSizeDTO {
    //이미지 total
    private Long totalImageSize;

    //영상 total
    private Long totalVideoSize;
}
