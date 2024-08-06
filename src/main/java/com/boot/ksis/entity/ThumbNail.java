package com.boot.ksis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "thumbNail")
@Getter
@Setter
public class ThumbNail {
    //썸네일 id
    @Id
    @Column(name = "thumbNail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long thumbNailId;

    //원본 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_resource_id")
    private OriginalResource originalResource;

    //경로
    private String filePath;

    //용량(KB 단위)
    private int fileSize;
}
