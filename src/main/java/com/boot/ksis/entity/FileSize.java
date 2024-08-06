package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "file_size")
@Getter
@Setter
public class FileSize {
    @Id
    @Column(name = "file_size_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileSizeId;

    //이미지 최대 용량(MB 단위)
    @ColumnDefault("10")
    private int imageMaxSize;

    //영상 최대 용량(MB 단위)
    @ColumnDefault("50")
    private int videoMaxSize;

    //이미지 총 용량
    private Long totalImage;

    //영상 총 용량
    private Long totalVideo;
}
