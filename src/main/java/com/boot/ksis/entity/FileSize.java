package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "file_size")
@Getter
@Setter
@DynamicInsert
public class FileSize {
    @Id
    @Column(name = "file_size_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileSizeId;

    //이미지 최대 용량(MB 단위)
    @ColumnDefault("10")
    private int imageMaxSize;

    //영상 최대 용량(MB 단위)
    @ColumnDefault("500")
    private int videoMaxSize;

    //원본 이미지 총 용량
    @ColumnDefault("0")
    private Long totalImage;

    //인코딩 이미지 총 용량
    @ColumnDefault("0")
    private Long totalEncodedImage;

    //원본 영상 총 용량
    @ColumnDefault("0")
    private Long totalVideo;

    //인코딩 영상 총 용량
    @ColumnDefault("0")
    private Long totalEncodedVideo;

    //원본 이미지 수
    @ColumnDefault("0")
    private Long countImage;

    //인코딩 이미지 수
    @ColumnDefault("0")
    private Long countEncodedImage;

    //원본 영상 수
    @ColumnDefault("0")
    private Long countVideo;

    //인코딩 영상 수
    @ColumnDefault("0")
    private Long countEncodedVideo;
}
