package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.ResourceThumbDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileBoardService {

    private final OriginalResourceRepository originalResourceRepository;
    private final ThumbNailRepository thumbNailRepository;

    // 모든 파일 조회
    public List<OriginalResource> getAllFiles() {
        return originalResourceRepository.findAll();
    }

    // 이미지 파일만 조회
    public List<OriginalResource> getImageFiles() {
        return originalResourceRepository.findByResourceType(ResourceType.IMAGE);
    }

    // 동영상 파일만 조회
    public List<OriginalResource> getVideoFiles() {
        return originalResourceRepository.findByResourceType(ResourceType.VIDEO);
    }

    // 파일 제목 수정
    public Optional<OriginalResource> updateFileTitle(Long id, String newTitle) {
        return originalResourceRepository.findById(id)
                .map(resource -> {
                    resource.setFileTitle(newTitle);
                    return originalResourceRepository.save(resource);
                });
    }

    // 파일 삭제
    public void deleteFile(Long id) {
        originalResourceRepository.deleteById(id);
    }

    // 썸네일 목록 조회
    public List<ResourceThumbDTO> getThumbnailList() {
        List<ThumbNail> thumbNails = thumbNailRepository.findAll();
        return thumbNails.stream()
                .map(t -> new ResourceThumbDTO(t.getThumbNailId(),
                        t.getOriginalResource().getFileTitle(),
                        t.getFilePath()))
                .collect(Collectors.toList());
    }
}
