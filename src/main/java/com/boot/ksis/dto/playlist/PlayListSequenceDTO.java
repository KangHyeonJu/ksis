package com.boot.ksis.dto.playlist;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class PlayListSequenceDTO {
    //인코딩 아이디
    private Long encodedResourceId;

    //순서
    private int sequence;

}
