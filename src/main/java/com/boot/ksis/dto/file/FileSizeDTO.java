package com.boot.ksis.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileSizeDTO {
    //이미지 용량
    private int imageMaxSize;
    //영상 용량
    private int videoMaxSize;
}
