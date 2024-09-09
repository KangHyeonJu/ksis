package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.entity.ThumbNail;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    //encodedResource 엔티티
    private final EncodedResourceRepository encodedResourceRepository;



    // 모든 원본 파일 조회
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
    // 특정 이미지 원본 파일 조회
    public List<EncodeListDTO> getResourceImgDtl(Long originalResourceId) {
        List<EncodeListDTO> resourceDetailListDTO = new ArrayList<>();

        OriginalResource originalResource = originalResourceRepository.findById(originalResourceId).orElse(null);

        List<EncodedResource> encodedResources = encodedResourceRepository.findByOriginalResource(originalResource);

        for (EncodedResource encodedResource : encodedResources) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);
            EncodeListDTO encode = new EncodeListDTO(encodedResource.getEncodedResourceId(), thumbNail.getFilePath(), encodedResource.getFileTitle(), encodedResource.getResolution(), encodedResource.getFormat(), encodedResource.getRegTime());
            resourceDetailListDTO.add(encode);
        }

        return resourceDetailListDTO;
    }

    // 특정 영상 원본 파일 조회
    public List<EncodeListDTO> getResourceVideoDtl(Long originalResourceId) {
        List<EncodeListDTO> resourceDetailListDTO = new ArrayList<>();

        OriginalResource originalResource = originalResourceRepository.findById(originalResourceId).orElse(null);

        List<EncodedResource> encodedResources = encodedResourceRepository.findByOriginalResource(originalResource);

        for (EncodedResource encodedResource : encodedResources) {
            EncodeListDTO encode = new EncodeListDTO(encodedResource.getEncodedResourceId(), encodedResource.getFilePath(), encodedResource.getFileTitle(), encodedResource.getResolution(), encodedResource.getFormat(), encodedResource.getRegTime());
            resourceDetailListDTO.add(encode);
        }

        return resourceDetailListDTO;
    }

    // 원본 이미지 파일만 조회
    public List<ResourceListDTO> getRsImageFiles() {
        List<ResourceListDTO> resourceListDTOList = new ArrayList<>();
        List<OriginalResource> originalResourceList = originalResourceRepository.findByResourceType(ResourceType.IMAGE);
        for (OriginalResource originalResource : originalResourceList) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);
            ResourceListDTO resource = new ResourceListDTO(originalResource.getOriginalResourceId(), thumbNail.getFilePath(), originalResource.getFileTitle(), originalResource.getResolution(), originalResource.getFormat(), originalResource.getRegTime());
            resourceListDTOList.add(resource);
        }
        //최종적으로 생성된 resourceListDTOList 반환
        return resourceListDTOList;
    }

    //인코딩 이미지 파일만 조회
    public List<EncodeListDTO> getEcImageFiles() {
        List<EncodeListDTO> encodeListDTOList = new ArrayList<>();
        List<EncodedResource> EncodedResourceList = encodedResourceRepository.findByResourceType(ResourceType.IMAGE);
        for (EncodedResource encodedResource : EncodedResourceList){
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());
            EncodeListDTO encoded = new EncodeListDTO(encodedResource.getEncodedResourceId(), thumbNail.getFilePath(),encodedResource.getFileTitle(), encodedResource.getResolution(), encodedResource.getFormat(), encodedResource.getRegTime());
            encodeListDTOList.add(encoded);
        }

        return encodeListDTOList;
    }

    //이미지 파일 인코딩 조회
    public ResourceListDTO getResourceFiles(Long originalResourceId) {
        OriginalResource originalResource = originalResourceRepository.findById(originalResourceId).orElse(null);

        return new ResourceListDTO(originalResource.getOriginalResourceId(), originalResource.getFilePath(), originalResource.getFileTitle(), originalResource.getResolution(), originalResource.getFormat(), originalResource.getRegTime());
    }

    // 원본 동영상 파일만 조회
    public List<ResourceListDTO> getRsVideoFiles() {
        List<ResourceListDTO> resourceListDTOList = new ArrayList<>();
        List<OriginalResource> originalResourceList = originalResourceRepository.findByResourceType(ResourceType.VIDEO);
        for (OriginalResource originalResource : originalResourceList) {
             ResourceListDTO resource = new ResourceListDTO(originalResource.getOriginalResourceId(), originalResource.getFilePath(), originalResource.getFileTitle(), originalResource.getResolution(), originalResource.getFormat(), originalResource.getRegTime());
                resourceListDTOList.add(resource);
        }
        return resourceListDTOList;
    }

    //인코딩 동영상 파일만 조회
    public List<EncodeListDTO> getEcVideoFiles() {
        List<EncodeListDTO> encodeListDTOList = new ArrayList<>();
        List<EncodedResource> EncodedResourceList = encodedResourceRepository.findByResourceType(ResourceType.VIDEO);
        for (EncodedResource encodedResource : EncodedResourceList){
           EncodeListDTO encoded = new EncodeListDTO(encodedResource.getEncodedResourceId(), encodedResource.getFilePath(),encodedResource.getFileTitle(), encodedResource.getResolution(), encodedResource.getFormat(), encodedResource.getRegTime());
               encodeListDTOList.add(encoded);
        }

        return encodeListDTOList;
    }

    // 원본 파일 제목 수정
    public Optional<OriginalResource> updateOrFileTitle(Long id, String newTitle) {
        // 주어진 ID로 원본 파일을 찾고, 제목을 수정 후 저장
        return originalResourceRepository.findById(id)
                .map(resource -> {
                    resource.setFileTitle(newTitle);    // 제목 수정
                    return originalResourceRepository.save(resource); // 수정된 파일 저장
                });
    }

    // 인코딩 파일 제목 수정
    public void updateErFileTitle(Long id, EncodeListDTO encodeListDTO) {
        EncodedResource encodedResource = encodedResourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다. id: " + id));

        encodedResource.setFileTitle(encodeListDTO.getFileTitle());
        encodedResourceRepository.save(encodedResource); // 변경된 내용을 저장
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

    @Transactional
    //인코딩 파일 삭제
    public void deleteEncodedFile(Long id) {
        EncodedResource encodedResource = encodedResourceRepository.findByEncodedResourceId(id);
        // 인코딩 파일 삭제
        encodedResourceRepository.deleteById(id);
    }
}





