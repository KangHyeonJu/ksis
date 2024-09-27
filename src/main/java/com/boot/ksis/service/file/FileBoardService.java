package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.*;
import com.boot.ksis.repository.file.FileSizeRepository;
import com.boot.ksis.repository.playlist.PlaylistSequenceRepository;
import com.boot.ksis.repository.signage.DeviceEncodeMapRepository;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import com.boot.ksis.repository.upload.EncodedResourceRepository;
import com.boot.ksis.repository.upload.OriginalResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileBoardService {

    private final DeviceEncodeMapRepository deviceEncodeMapRepository;
    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${encodingLocation}")
    String encodingLocation;

    @Value("${thumbnailsLocation}")
    String thumbnailsLocation;

    // OriginalResource 엔티티를 데이터베이스에서 조회하거나 저장하는 데 사용되는 레포지토리
    private final OriginalResourceRepository originalResourceRepository;

    // ThumbNail 엔티티를 데이터베이스에서 조회하거나 삭제하는 데 사용되는 레포지토리
    private final ThumbNailRepository thumbNailRepository;

    //encodedResource 엔티티
    private final EncodedResourceRepository encodedResourceRepository;

    private final FileSizeRepository fileSizeRepository;

    private final PlaylistSequenceRepository playlistSequenceRepository;


    // 조회
    // 모든 원본 파일 조회(업로드된 원본 파일 목록 조회)
    public List<ResourceListDTO> getAllFiles() {
        // 모든 OriginalResource 엔티티를 조회하고, ResourceListDTO로 변환하여 반환
        return originalResourceRepository.findByResourceStatus(ResourceStatus.COMPLETED).stream()
                .map(resource -> new ResourceListDTO(
                        resource.getOriginalResourceId(), // 원본 파일의 ID
                        resource.getFilePath(),           // 파일의 경로
                        resource.getFileTitle(),          // 파일의 제목
                        resource.getResolution(),         // 파일의 해상도
                        resource.getFormat(),           // 파일의 포맷
                        resource.getRegTime()))   //등록일
                .collect(Collectors.toList());     // 변환된 DTO 리스트를 반환
    }

    // 본인이 업로드한 원본 이미지 파일만 조회
    public List<ResourceListDTO> getRsImageFiles(Account accountId) {
        List<ResourceListDTO> resourceListDTOList = new ArrayList<>();

        // accountId로 본인이 업로드한 이미지만 조회
        List<OriginalResource> originalResourceList = originalResourceRepository.findByAccountAndResourceStatusAndResourceTypeOrderByRegTimeDesc(
                accountId, ResourceStatus.COMPLETED, ResourceType.IMAGE);

        for (OriginalResource originalResource : originalResourceList) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // ResourceListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO resource = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );
            resourceListDTOList.add(resource);
        }

        // 최종적으로 생성된 resourceListDTOList 반환
        return resourceListDTOList;
    }


    // 인코딩된 이미지 파일만 조회
    public List<EncodeListDTO> getEcImageFiles(Account accountId) {
        List<EncodeListDTO> encodeListDTOList = new ArrayList<>();

        // 로그인한 사용자의 originalResource를 먼저 필터링
        List<OriginalResource> originalResourceList = originalResourceRepository.findByAccountAndResourceStatusOrderByRegTimeDesc(accountId, ResourceStatus.COMPLETED);

        // 해당 originalResourceId와 연관된 인코딩 리소스 조회
        List<EncodedResource> encodedResourceList = encodedResourceRepository.findByOriginalResourceInAndResourceStatusAndResourceTypeOrderByRegTimeDesc(
                originalResourceList, ResourceStatus.COMPLETED, ResourceType.IMAGE);

        // 필터링된 encodedResource로 DTO 생성
        for (EncodedResource encodedResource : encodedResourceList) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

            // EncodeListDTO 객체 생성 후 리스트에 추가
            EncodeListDTO encoded = new EncodeListDTO(
                    encodedResource.getEncodedResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    encodedResource.getFileTitle(),
                    encodedResource.getResolution(),
                    encodedResource.getFormat(),
                    encodedResource.getRegTime()
            );

            encodeListDTOList.add(encoded);
        }

        return encodeListDTOList;
    }


    // 본인이 업로드한, 업로드가 완료된 원본 동영상 파일만 조회
    public List<ResourceListDTO> getRsVideoFiles(Account accountId) {
        List<ResourceListDTO> resourceListDTOList = new ArrayList<>();

        // accountId로 본인이 업로드한 영상 조회
        List<OriginalResource> originalResourceList = originalResourceRepository.findByAccountAndResourceStatusAndResourceTypeOrderByRegTimeDesc(
                accountId, ResourceStatus.COMPLETED, ResourceType.VIDEO);
        for (OriginalResource originalResource : originalResourceList) {
            ResourceListDTO resource = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime());
            resourceListDTOList.add(resource);
        }
        return resourceListDTOList;
    }

    //본인이 올린 동영상 파일만 조회(인코딩이 완료된것만)
    public List<EncodeListDTO> getEcVideoFiles(Account accountId) {
        List<EncodeListDTO> encodeListDTOList = new ArrayList<>();

        // 로그인한 사용자의 originalResource를 먼저 필터링
        List<OriginalResource> originalResourceList = originalResourceRepository.findByAccountAndResourceStatusOrderByRegTimeDesc(accountId, ResourceStatus.COMPLETED);

        List<EncodedResource> EncodedResourceList = encodedResourceRepository.findByOriginalResourceInAndResourceStatusAndResourceTypeOrderByRegTimeDesc(
                originalResourceList, ResourceStatus.COMPLETED, ResourceType.VIDEO);
        for (EncodedResource encodedResource : EncodedResourceList){
            EncodeListDTO encoded = new EncodeListDTO(encodedResource.getEncodedResourceId(),
                    encodedResource.getFilePath(),
                    encodedResource.getFileTitle(),
                    encodedResource.getResolution(),
                    encodedResource.getFormat(),
                    encodedResource.getRegTime());
            encodeListDTOList.add(encoded);
        }

        return encodeListDTOList;
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



    //이미지 파일 인코딩 조회
    public ResourceListDTO getResourceFiles(Long originalResourceId) {
        OriginalResource originalResource = originalResourceRepository.findById(originalResourceId).orElse(null);

        return new ResourceListDTO(originalResource.getOriginalResourceId(), originalResource.getFilePath(), originalResource.getFileTitle(), originalResource.getResolution(), originalResource.getFormat(), originalResource.getRegTime());
    }



    //수정
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



    //삭제

    // 스토리지에서 파일 삭제 메서드
    private void deleteFileFromStorage(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("파일 삭제 성공 위치 : " + filePath);
            if (!deleted) {
                throw new RuntimeException("파일 삭제에 실패했습니다: " + filePath);
            }
        } else {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + filePath);
        }
    }


    // 파일 삭제 및 관련된 썸네일 삭제
    @Transactional
    public void deleteFile(Long id) {
        OriginalResource originalResource = originalResourceRepository.findByOriginalResourceId(id);
        List<EncodedResource> encodedResources = encodedResourceRepository.findByOriginalResource(originalResource);
        ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

        // 원본 파일 삭제
        deleteFileFromStorage(uploadLocation+originalResource.getFileName());

        // 리스트 내의 인코딩된 파일 삭제
        for (EncodedResource encodedResource : encodedResources) {
            deleteFileFromStorage(encodingLocation+encodedResource.getFileName());
        }

        // 썸네일 파일 삭제
        // 썸네일 파일 경로에서 /file/thumbnails/ 부분 제거
        String filePathWithoutPrefix = thumbNail.getFilePath().replace("/file/thumbnails/", "");

        // 썸네일 로컬 스토리지 경로 생성
        String inputFilePath = thumbnailsLocation + filePathWithoutPrefix;

        // 생성된 썸네일 로컬 파일 경로로 파일 삭제 메서드 호출
        deleteFileFromStorage(inputFilePath);


        // 시퀀스 DB 삭제 (각 EncodedResource에 대해 개별적으로 삭제)
        for (EncodedResource encodedResource : encodedResources) {
            playlistSequenceRepository.deleteByEncodedResource(encodedResource);
        }

        // deviceEncodedMap DB 삭제 (각 EncodedResource ID에 대해 개별적으로 삭제)
        for (EncodedResource encodedResource : encodedResources) {
            deviceEncodeMapRepository.deleteByEncodedResourceId(encodedResource.getEncodedResourceId());
        }

        // 관련된 썸네일 DB 삭제
        thumbNailRepository.deleteByOriginalResource(originalResource);

        // 연관된 인코딩 파일 DB 삭제
        encodedResourceRepository.deleteByOriginalResource(originalResource);

        // 원본 파일 DB 삭제
        originalResourceRepository.deleteById(id);




        // 용량 삭제
        FileSize fileSize = fileSizeRepository.findByFileSizeId(1);

        if (fileSize != null) {
            long totalEncodedFileSize = 0;

            for (EncodedResource encodedResource : encodedResources) {
                totalEncodedFileSize += encodedResource.getFileSize();
            }
            // 리소스 타입 확인 후 용량 처리
            // 리소스가 이미지일 경우 TotalImage에서 용량 차감
            if (originalResource.getResourceType() == ResourceType.IMAGE) {
                fileSize.setTotalImage(
                        fileSize.getTotalImage() - originalResource.getFileSize() - totalEncodedFileSize - thumbNail.getFileSize()
                );
            }
            // 리소스가 비디오일 경우 TotalVideo에서 용량 차감
            else if(originalResource.getResourceType() == ResourceType.VIDEO) {
                fileSize.setTotalVideo(
                        fileSize.getTotalVideo() - originalResource.getFileSize() - totalEncodedFileSize - thumbNail.getFileSize()
                );
            }
        }

    }

    //인코딩 파일 삭제 관련 파일
    @Transactional
    public void deleteEncodedFile(Long id) {
        EncodedResource encodedResource = encodedResourceRepository.findByEncodedResourceId(id);

        // 인코딩된 파일 삭제
        deleteFileFromStorage(encodingLocation+encodedResource.getFileName());

        // 시퀀스 DB 삭제 (각 EncodedResource에 대해 개별적으로 삭제)
        playlistSequenceRepository.deleteByEncodedResource(encodedResource);


        // deviceEncodedMap DB 삭제 (각 EncodedResource ID에 대해 개별적으로 삭제)
        deviceEncodeMapRepository.deleteByEncodedResourceId(encodedResource.getEncodedResourceId());


        // 인코딩 파일 DB 삭제
        encodedResourceRepository.deleteById(id);


            // 용량 삭제
            FileSize fileSize = fileSizeRepository.findByFileSizeId(1);

        if (fileSize != null ) {
            // 리소스 타입 확인 후 용량 처리
            if (encodedResource.getResourceType() == ResourceType.IMAGE) {
                // 리소스가 이미지일 경우 TotalImage에서 용량 차감
                fileSize.setTotalImage(fileSize.getTotalImage() - encodedResource.getFileSize());
            } else if (encodedResource.getResourceType() == ResourceType.VIDEO) {
                // 리소스가 비디오일 경우 TotalVideo에서 용량 차감
                fileSize.setTotalVideo(fileSize.getTotalVideo() - encodedResource.getFileSize());
            }

            // 용량 정보 저장
            fileSizeRepository.save(fileSize);
        }
    }



}





