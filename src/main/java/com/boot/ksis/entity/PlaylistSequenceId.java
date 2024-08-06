package com.boot.ksis.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@Setter
public class PlaylistSequenceId implements Serializable {
    //재생목록 id
    private Long playlistId;

    //순서
    private int sequence;

}
