package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "playlist_sequence")
@IdClass(PlaylistSequenceId.class)
@Getter
@Setter
public class PlaylistSequence {
    //재생목록 id
    @Id
    private Long playlistId;

    //순서
    @Id
    private int sequence;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    private PlayList playList;

    @ManyToOne
    @JoinColumn(name = "encoded_resource_id")
    private EncodedResource encodedResource;
}
