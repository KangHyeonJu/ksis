package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "playlist")
@Getter
@Setter
public class PlayList extends BaseEntity{
    //재생목록 id
    @Id
    @Column(name = "playlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playlistId;

    //디바이스 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    //슬라이드 시간
    @ColumnDefault("5")
    private int slideTime;

    //제목
    @Column(nullable = false, length = 50)
    private String fileTitle;

    //재생여부
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    @ColumnDefault("false")
    private boolean isDefault;
}
