package com.boot.ksis.dto.playlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PlayListDTO {
    //아이디
    private Long playlistId;

    //제목
    private String title;

    //등록일
    private LocalDateTime regTime;

    //재생여부
    private Boolean play;

    //재생시간
    private int playTime;
}
