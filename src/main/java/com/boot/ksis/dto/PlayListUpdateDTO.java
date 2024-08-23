package com.boot.ksis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class PlayListUpdateDTO {
    //인코딩리소스
    List<SignageResourceDTO> SignageResourceDTO;

    //재생목록 제목
    private String fileTitle;

    //슬라이드 시간
    private int slideTime;
}
