package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.OriginalResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class OriginalResourceService {

    private final OriginalResourceRepository originalResourceRepository;

    // 파일 메타데이터 저장
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

    // 상태를 COMPLETED로 업데이트
    public OriginalResource updateStatus(String uuidFileName){

        OriginalResource originalResource = originalResourceRepository.findByFileName(uuidFileName)
                .orElseThrow(() -> new RuntimeException("File not found: " + uuidFileName));

        // 상태를 COMPLETED로 변경
        originalResource.setResourceStatus(ResourceStatus.COMPLETED);

        // 변경된 상태를 데이터베이스에 저장
        originalResourceRepository.save(originalResource);

        return originalResource;
    }

}
