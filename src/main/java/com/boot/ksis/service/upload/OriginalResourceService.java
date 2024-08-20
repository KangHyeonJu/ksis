package com.boot.ksis.service.upload;

import com.boot.ksis.constant.ResourceStatus;
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
        int dotIndex = title.lastIndexOf(".");  // 마지막 '.'의 인덱스 찾기
        String titleWithoutExtension = (dotIndex != -1) ? title.substring(0, dotIndex) : title;

        System.out.println("dd" + titleWithoutExtension);

        // 파일 제목으로 리소스 찾기
        OriginalResource resource = originalResourceRepository.findByFileTitle(titleWithoutExtension);
        if (resource != null) {
            resource.setResourceStatus(status);
            originalResourceRepository.save(resource);
        } else {
            System.out.println("Resource not found with title: " + title);
        }
    }

}
