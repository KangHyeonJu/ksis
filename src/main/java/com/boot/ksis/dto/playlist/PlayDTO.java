package com.boot.ksis.dto.playlist;

import com.boot.ksis.constant.ResourceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
@Builder
public class PlayDTO {
    //인코딩 id
    private Long encodedResourceId;

    //순서
    private int sequence;

    //인코딩 경로
    private String filePath;

    //인코딩 유형
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    //재생시간
    private float playTime;
}
