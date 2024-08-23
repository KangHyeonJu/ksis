package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
