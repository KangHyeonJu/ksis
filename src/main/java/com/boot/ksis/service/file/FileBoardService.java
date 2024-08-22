package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.dto.file.ResourceThumbDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileBoardService {

    // OriginalResource 엔티티를 데이터베이스에서 조회하거나 저장하는 데 사용되는 레포지토리
    private final OriginalResourceRepository originalResourceRepository;

    // ThumbNail 엔티티를 데이터베이스에서 조회하거나 삭제하는 데 사용되는 레포지토리
    private final ThumbNailRepository thumbNailRepository;

    // 모든 파일 조회
    public List<ResourceListDTO> getAllFiles() {
        // 모든 OriginalResource 엔티티를 조회하고, ResourceListDTO로 변환하여 반환
        return originalResourceRepository.findAll().stream()
                .map(resource -> new ResourceListDTO(
                        resource.getOriginalResourceId(), // 원본 파일의 ID
                        resource.getFilePath(),           // 파일의 경로
                        resource.getFileTitle(),          // 파일의 제목
                        resource.getResolution(),         // 파일의 해상도
                        resource.getFormat(),           // 파일의 포맷
                        resource.getRegTime()))   //등록일
                .collect(Collectors.toList());     // 변환된 DTO 리스트를 반환
    }

    // 이미지 파일만 조회
    public List<ResourceListDTO> getImageFiles() {
        // 이미지 파일만 조회하고, ResourceListDTO로 변환하여 반환
        return originalResourceRepository.findByResourceType(ResourceType.IMAGE).stream()
                .map(resource -> new ResourceListDTO(
                        resource.getOriginalResourceId(), // 원본 파일의 ID
                        resource.getFilePath(),           // 파일의 경로
                        resource.getFileTitle(),          // 파일의 제목
                        resource.getResolution(),         // 파일의 해상도
                        resource.getFormat(),           // 파일의 포맷
                        resource.getRegTime()))   //등록일
                .collect(Collectors.toList());     // 변환된 DTO 리스트를 반환
    }

    // 동영상 파일만 조회
    public List<ResourceListDTO> getVideoFiles() {
        // 동영상 파일만 조회하고, ResourceListDTO로 변환하여 반환
        return originalResourceRepository.findByResourceType(ResourceType.VIDEO).stream()
                .map(resource -> new ResourceListDTO(
                        resource.getOriginalResourceId(), // 원본 파일의 ID
                        resource.getFilePath(),           // 파일의 경로
                        resource.getFileTitle(),          // 파일의 제목
                        resource.getResolution(),         // 파일의 해상도
                        resource.getFormat(),           // 파일의 포맷
                        resource.getRegTime()))   //등록일
                .collect(Collectors.toList());     // 변환된 DTO 리스트를 반환
    }

    // 파일 제목 수정
    public Optional<OriginalResource> updateFileTitle(Long id, String newTitle) {
        // 주어진 ID로 원본 파일을 찾고, 제목을 수정 후 저장
        return originalResourceRepository.findById(id)
                .map(resource -> {
                    resource.setFileTitle(newTitle);    // 제목 수정
                    return originalResourceRepository.save(resource); // 수정된 파일 저장
                });
    }

    @Transactional
    // 파일 삭제 및 관련된 썸네일 삭제
    public void deleteFile(Long id) {
       OriginalResource originalResource = originalResourceRepository.findByOriginalResourceId(id);

        // 먼저 관련된 썸네일을 삭제
        thumbNailRepository.deleteByOriginalResource(originalResource);

        // 원본 파일 삭제
        originalResourceRepository.deleteById(id);
    }

    // 썸네일 목록 조회
    public List<ResourceThumbDTO> getThumbnailList() {
        // 모든 썸네일을 조회하고, ResourceThumbDTO로 변환하여 반환
        List<ThumbNail> thumbNails = thumbNailRepository.findAll();
        return thumbNails.stream()
                .map(t -> new ResourceThumbDTO(
                        t.getThumbNailId(),            // 썸네일의 ID
                        t.getOriginalResource().getFileTitle(), // 원본 파일의 제목
                        t.getFilePath()))              // 썸네일의 경로
                .collect(Collectors.toList());   // 변환된 DTO 리스트를 반환
    }
}

