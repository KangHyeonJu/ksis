package com.boot.ksis.entity;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "original_resource")
@Getter
@Setter
public class OriginalResource extends BaseEntity {
    //원본 id
    @Id
    @Column(name = "original_resource_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long originalResourceId;

    //재생 시간
    private LocalTime playTime;

    //해상도
    @Column(nullable = false, length = 50)
    private String resolution;

    //제목
    @Column(nullable = false, length = 50)
    private String fileTitle;

    //파일명
    private String fileName;

    //포맷
    @Column(nullable = false, length = 20)
    private String format;

    //용량(KB 단위)
    private int fileSize;

    //경로
    private String filePath;

    //유형
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    //상태
    @Enumerated(EnumType.STRING)
    private ResourceStatus resourceStatus;
}
