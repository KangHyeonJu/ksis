package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.OriginalResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OriginalResourceService {

    private final OriginalResourceRepository originalResourceRepository;

    public void saveToDatabase(OriginalResourceDTO originalResourceDTO){
        OriginalResource originalResource = new OriginalResource();
        originalResource.setFileTitle(originalResourceDTO.getTitle());
        originalResource.setFileName(originalResourceDTO.getFilename());
        originalResource.setFilePath(originalResourceDTO.getFilePath());
        originalResource.setPlayTime(originalResourceDTO.getPlayTime());
        originalResource.setFormat(originalResourceDTO.getFormat());
        originalResource.setResolution(originalResourceDTO.getResolution());
        originalResource.setFileSize((int)(originalResourceDTO.getFileSize() / 1024)); // KB 단위로 저장
        originalResource.setResourceStatus(originalResourceDTO.getStatus());
        originalResource.setResourceType(originalResourceDTO.getResourceType());

        // 엔티티를 데이터베이스에 저장
        originalResourceRepository.save(originalResource);
    }

}
