package com.boot.ksis.dto.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Device;
import com.boot.ksis.entity.OriginalResource;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class OriginalResourceDTO {
    private String filename; // 파일 이름(확장자까지)
    private String fileTitle; // 제목
    private String filePath; // 경로
    private float playTime; // 재생시간
    private String format; // 포맷
    private String resolution; // 해상도
    private long fileSize; // 파일용량
    private ResourceStatus status; // 업로드 상태
    private ResourceType resourceType; // 이미지, 영상 구분

    // 엔티티에 자동으로 넣어주는 코드
    private static ModelMapper modelMapper = new ModelMapper();

    public OriginalResource createNewSignage(){
        return modelMapper.map(this, OriginalResource.class);
    }

}
