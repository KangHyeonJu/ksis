package com.boot.ksis.entity;

import com.boot.ksis.dto.playlist.PlayListAddDTO;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "playlist")
@Getter
@Setter
public class PlayList extends BaseEntity {
    //재생목록 id
    @Id
    @Column(name = "playlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;

    //디바이스 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    //계정 아이디
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    //슬라이드 시간
    @ColumnDefault("5")
    private int slideTime;

    //제목
    @Column(nullable = false, length = 50)
    private String fileTitle;

    //재생여부
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    @ColumnDefault("false")
    private Boolean isDefault;

    public void updatePlaylist(PlayListAddDTO playListAddDTO){
        this.fileTitle = playListAddDTO.getFileTitle();
        this.slideTime = playListAddDTO.getSlideTime();
    }
}
