package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.ResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OriginalResourceService {

    private final OriginalResourceRepository originalResourceRepository;

    public void saveFileMetadata(ResourceDTO dto) {
        OriginalResource resource = new OriginalResource();
        resource.setFileName(dto.getFilename());
        resource.setFileTitle(dto.getTitle());
        resource.setFormat(dto.getFormat());
        resource.setResolution(dto.getResolution());
        resource.setFileSize((int) dto.getFileSize() / 1024);
        resource.setResourceStatus(ResourceStatus.valueOf(dto.getStatus()));

        originalResourceRepository.save(resource);
    }

    public void updateFileStatus(String title, ResourceStatus status) {
        // 파일 제목으로 리소스 찾기
        OriginalResource resource = originalResourceRepository.findByFileTitle(title);
        if (resource != null) {
            // 상태 업데이트
            resource.setResourceStatus(status);
            originalResourceRepository.save(resource);
        }
    }

}
