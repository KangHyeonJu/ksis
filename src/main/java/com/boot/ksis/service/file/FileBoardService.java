package com.boot.ksis.service.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.*;
import com.boot.ksis.entity.Log.ActivityLog;
import com.boot.ksis.repository.file.FileEncodedRepository;
import com.boot.ksis.repository.file.FileOriginRepository;
import com.boot.ksis.repository.file.FileSizeRepository;
import com.boot.ksis.repository.log.ActivityLogRepository;
import com.boot.ksis.repository.playlist.PlaylistSequenceRepository;
import com.boot.ksis.repository.signage.DeviceEncodeMapRepository;
import com.boot.ksis.repository.signage.ThumbNailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


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
    private final FileOriginRepository fileOriginRepository;
    //encodedResource 엔티티
    private final FileEncodedRepository fileEncodedRepository;

    // ThumbNail 엔티티를 데이터베이스에서 조회하거나 삭제하는 데 사용되는 레포지토리
    private final ThumbNailRepository thumbNailRepository;

    private final FileSizeRepository fileSizeRepository;

    private final PlaylistSequenceRepository playlistSequenceRepository;

    private final ActivityLogRepository activityLogRepository;


    // 조회
    // 모든 원본 파일 조회(업로드된 원본 파일 목록 조회)
    public List<ResourceListDTO> getAllFiles() {
        List<ResourceListDTO> resourceListDTOS = new ArrayList<>();

        List<OriginalResource> originalResources = fileOriginRepository.findByResourceStatusAndIsActive(ResourceStatus.COMPLETED, true);

        for (OriginalResource originalResource : originalResources) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // ResourceListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO resource = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );
            resourceListDTOS.add(resource);
        }

        // 최종적으로 생성된 resourceListDTOList 반환
        return resourceListDTOS;
    }

    // 본인이 업로드한  활성화 된 원본 이미지 파일만 조회
    public Page<ResourceListDTO> getRsActiveImageFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role, String startTime, String endTime) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<OriginalResource> resourceListDTOPage;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.IMAGE, true, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActive
                            (startDateTime, endDateTime, ResourceStatus.COMPLETED, ResourceType.IMAGE, true, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.IMAGE, true, searchTerm, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, true, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, true, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.IMAGE, true, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActiveAndAccount
                            (startDateTime, endDateTime, ResourceStatus.COMPLETED, ResourceType.IMAGE, true, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.IMAGE, true, searchTerm,  accountId, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, true, accountId, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, true, accountId, pageable);}
        }

        List<ResourceListDTO> originalListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (OriginalResource originalResource : resourceListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // OriginListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO origin = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );

            originalListDTOList.add(origin);
        }
        return new PageImpl<>(originalListDTOList, pageable, resourceListDTOPage.getTotalElements());
    }

    // 인코딩된 이미지 파일만 조회
    public Page<EncodeListDTO> getEcActiveImageFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<EncodedResource> encodeListDTOPage;

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.IMAGE, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    encodeListDTOPage = fileEncodedRepository.searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCase(searchTerm, ResourceStatus.COMPLETED, ResourceType.IMAGE, pageable);
                }else if(searchCategory.equals("resolution")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.IMAGE, searchTerm, pageable);
                }else{
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceType(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, pageable);}
            }else{
                encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceType(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCaseAndOriginalResource_Account(ResourceStatus.COMPLETED, ResourceType.IMAGE, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    encodeListDTOPage = fileEncodedRepository.searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCaseAndOriginalResource_Account(searchTerm, ResourceStatus.COMPLETED, ResourceType.IMAGE, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCaseAndOriginalResource_Account(ResourceStatus.COMPLETED, ResourceType.IMAGE, searchTerm,  accountId, pageable);
                }else{
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndOriginalResource_AccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, accountId, pageable);}
            }else{
                encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndOriginalResource_AccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, accountId, pageable);}
        }

        List<EncodeListDTO> encodedListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (EncodedResource encodedResource : encodeListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

            // EncodeListDTO 객체 생성 후 리스트에 추가
            EncodeListDTO encoded = new EncodeListDTO(
                    encodedResource.getEncodedResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    encodedResource.getFilePath(),
                    encodedResource.getFileTitle(),
                    encodedResource.getResolution(),
                    encodedResource.getFormat(),
                    encodedResource.getRegTime()
            );

            encodedListDTOList.add(encoded);
        }
        return new PageImpl<>(encodedListDTOList, pageable, encodeListDTOPage.getTotalElements());
    }


    // 본인이 업로드한, 업로드가 완료된 활성화 원본 동영상 파일만 조회
    public Page<ResourceListDTO> getRsActiveVideoFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role, String startTime, String endTime) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<OriginalResource> resourceListDTOPage;

        // 시작시간과 끝시간을 LocalDateTime으로 파싱
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            if (startTime != null && !startTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, formatter);
                startDateTime = startDate.atStartOfDay(); // 00:00:00으로 변환
            }
            if (endTime != null && !endTime.isEmpty()) {
                LocalDate endDate = LocalDate.parse(endTime, formatter);
                endDateTime = endDate.atTime(23, 59, 59); // 23:59:59으로 변환
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("잘못된 날짜 형식입니다.");
        }

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, true, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActive
                            (startDateTime, endDateTime, ResourceStatus.COMPLETED, ResourceType.VIDEO, true, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, true, searchTerm, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, true, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, true, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, true, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActiveAndAccount
                            (startDateTime, endDateTime, ResourceStatus.COMPLETED, ResourceType.VIDEO, true, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, true, searchTerm,  accountId, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, true, accountId, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, true, accountId, pageable);}
        }

        List<ResourceListDTO> originalListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (OriginalResource originalResource : resourceListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // OriginListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO origin = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );

            originalListDTOList.add(origin);
        }
        return new PageImpl<>(originalListDTOList, pageable, resourceListDTOPage.getTotalElements());
    }



    //본인이 올린 동영상 파일만 조회(인코딩이 완료된것만)
    public Page<EncodeListDTO> getEcActiveVideoFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<EncodedResource> encodeListDTOPage;

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.VIDEO, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    encodeListDTOPage = fileEncodedRepository.searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCase(searchTerm, ResourceStatus.COMPLETED, ResourceType.VIDEO, pageable);
                }else if(searchCategory.equals("resolution")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.VIDEO, searchTerm, pageable);
                }else{
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceType(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, pageable);}
            }else{
                encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceType(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCaseAndOriginalResource_Account(ResourceStatus.COMPLETED, ResourceType.VIDEO, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    encodeListDTOPage = fileEncodedRepository.searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCaseAndOriginalResource_Account(searchTerm, ResourceStatus.COMPLETED, ResourceType.VIDEO, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCaseAndOriginalResource_Account(ResourceStatus.COMPLETED, ResourceType.VIDEO, searchTerm,  accountId, pageable);
                }else{
                    encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndOriginalResource_AccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, accountId, pageable);}
            }else{
                encodeListDTOPage = fileEncodedRepository.findByResourceStatusAndResourceTypeAndOriginalResource_AccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, accountId, pageable);}
        }

        List<EncodeListDTO> encodedListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (EncodedResource encodedResource : encodeListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(encodedResource.getOriginalResource());

            // EncodeListDTO 객체 생성 후 리스트에 추가
            EncodeListDTO encoded = new EncodeListDTO(
                    encodedResource.getEncodedResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    encodedResource.getFilePath(),
                    encodedResource.getFileTitle(),
                    encodedResource.getResolution(),
                    encodedResource.getFormat(),
                    encodedResource.getRegTime()
            );

            encodedListDTOList.add(encoded);
        }
        return new PageImpl<>(encodedListDTOList, pageable, encodeListDTOPage.getTotalElements());
    }



    //비활성화
    // 본인이 업로드한  활성화 된 원본 이미지 파일만 조회
    public Page<ResourceListDTO> getDeactiveImageFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<OriginalResource> resourceListDTOPage;

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.IMAGE, false, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCase(searchTerm, ResourceStatus.COMPLETED, ResourceType.IMAGE, false, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCase(ResourceStatus.COMPLETED, ResourceType.IMAGE, false, searchTerm, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, false, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, false, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCaseAndAccount(ResourceStatus.COMPLETED, ResourceType.IMAGE, false, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCaseAndAccount(searchTerm, ResourceStatus.COMPLETED, ResourceType.IMAGE, false, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCaseAndAccount(ResourceStatus.COMPLETED, ResourceType.IMAGE, false, searchTerm,  accountId, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.IMAGE, false, accountId, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.IMAGE, false, accountId, pageable);}
        }

        List<ResourceListDTO> originalListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (OriginalResource originalResource : resourceListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // OriginListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO origin = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );

            originalListDTOList.add(origin);
        }
        return new PageImpl<>(originalListDTOList, pageable, resourceListDTOPage.getTotalElements());
    }


    public Page<ResourceListDTO> getDeactiveVideoFiles(int page, int size, String searchTerm, String searchCategory, Account accountId, Role role) {


        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<OriginalResource> resourceListDTOPage;

        if(role == Role.ADMIN){
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, false, searchTerm, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCase
                            (searchTerm, ResourceStatus.COMPLETED, ResourceType.VIDEO, false, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCase
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, false, searchTerm, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, false, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActive(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, false, pageable);}
        }else {
            if(searchCategory != null && !searchCategory.isEmpty()) {
                if(searchCategory.equals("fileTitle")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, false, searchTerm,  accountId, pageable);
                }else if(searchCategory.equals("regTime")){
                    resourceListDTOPage = fileOriginRepository.searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCaseAndAccount
                            (searchTerm, ResourceStatus.COMPLETED, ResourceType.VIDEO, false, accountId, pageable);
                }else if(searchCategory.equals("resolution")){
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCaseAndAccount
                            (ResourceStatus.COMPLETED, ResourceType.VIDEO, false, searchTerm,  accountId, pageable);
                }else{
                    resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                            ResourceStatus.COMPLETED, ResourceType.VIDEO, false, accountId, pageable);}
            }else{
                resourceListDTOPage = fileOriginRepository.findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase(
                        ResourceStatus.COMPLETED, ResourceType.VIDEO, false, accountId, pageable);}
        }

        List<ResourceListDTO> originalListDTOList = new ArrayList<>();

        // 필터링된 encodedResource로 DTO 생성
        for (OriginalResource originalResource : resourceListDTOPage) {
            ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);

            // OriginListDTO 객체 생성 후 리스트에 추가
            ResourceListDTO origin = new ResourceListDTO(
                    originalResource.getOriginalResourceId(),
                    thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 있을 경우 경로, 없으면 null
                    originalResource.getFilePath(),
                    originalResource.getFileTitle(),
                    originalResource.getResolution(),
                    originalResource.getFormat(),
                    originalResource.getRegTime()
            );

            originalListDTOList.add(origin);
        }
        return new PageImpl<>(originalListDTOList, pageable, resourceListDTOPage.getTotalElements());
    }

    // 특정 이미지 원본 파일 조회
    public Page<EncodeListDTO> getResourceImgDtl(Long originalResourceId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        // 원본 리소스 조회
        OriginalResource originalResource = fileOriginRepository.findById(originalResourceId).orElse(null);

        // 원본 리소스가 존재하지 않을 경우 빈 페이지 반환
        if (originalResource == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // 인코딩된 리소스 조회
        Page<EncodedResource> encodedResources = fileEncodedRepository.findByOriginalResourceAndResourceStatusAndResourceType(
                originalResource, ResourceStatus.COMPLETED, ResourceType.IMAGE, pageable);

        // EncodeListDTO 리스트 생성
        List<EncodeListDTO> resourceDetailList = new ArrayList<>();

        // 인코딩된 리소스가 존재할 경우
        if (!encodedResources.isEmpty()) {
            for (EncodedResource encodedResource : encodedResources) {
                ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);
                EncodeListDTO encode = new EncodeListDTO(
                        encodedResource.getEncodedResourceId(),
                        thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 존재할 경우
                        originalResource.getFilePath(),
                        encodedResource.getFileTitle(),
                        encodedResource.getResolution(),
                        encodedResource.getFormat(),
                        encodedResource.getRegTime()
                );
                resourceDetailList.add(encode);
            }
        } else {
            // 인코딩된 리소스가 없을 경우 원본 리소스 정보만 포함
            EncodeListDTO encode = EncodeListDTO.builder()
                    .filePath(originalResource.getFilePath())
                    .build();
            resourceDetailList.add(encode);
        }

        // EncodeListDTO 리스트를 Page로 변환하여 반환
        return new PageImpl<>(resourceDetailList, pageable, encodedResources.getTotalElements());

    }

    // 특정 영상 원본 파일 조회
    public Page<EncodeListDTO> getResourceVideoDtl(Long originalResourceId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));

        // 원본 리소스 조회
        OriginalResource originalResource = fileOriginRepository.findById(originalResourceId).orElse(null);

        // 원본 리소스가 존재하지 않을 경우 빈 페이지 반환
        if (originalResource == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // 인코딩된 리소스 조회
        Page<EncodedResource> encodedResources = fileEncodedRepository.findByOriginalResourceAndResourceStatusAndResourceType(
                originalResource, ResourceStatus.COMPLETED, ResourceType.VIDEO, pageable);

        // EncodeListDTO 리스트 생성
        List<EncodeListDTO> resourceDetailList = new ArrayList<>();

        // 인코딩된 리소스가 존재할 경우
        if (!encodedResources.isEmpty()) {
            for (EncodedResource encodedResource : encodedResources) {
                ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);
                EncodeListDTO encode = new EncodeListDTO(
                        encodedResource.getEncodedResourceId(),
                        thumbNail != null ? thumbNail.getFilePath() : null,  // 썸네일이 존재할 경우
                        originalResource.getFilePath(),
                        encodedResource.getFileTitle(),
                        encodedResource.getResolution(),
                        encodedResource.getFormat(),
                        encodedResource.getRegTime()
                );
                resourceDetailList.add(encode);
            }
        } else {
            // 인코딩된 리소스가 없을 경우 원본 리소스 정보만 포함
            EncodeListDTO encode = EncodeListDTO.builder()
                    .filePath(originalResource.getFilePath())
                    .build();
            resourceDetailList.add(encode);
        }

        // EncodeListDTO 리스트를 Page로 변환하여 반환
        return new PageImpl<>(resourceDetailList, pageable, encodedResources.getTotalElements());
    }



    //이미지 파일 인코딩 조회
    public ResourceListDTO getResourceFiles(Long originalResourceId) {
        OriginalResource originalResource = fileOriginRepository.findById(originalResourceId).orElse(null);
        ThumbNail thumbNail = thumbNailRepository.findByOriginalResource(originalResource);
        return new ResourceListDTO(
                originalResource.getOriginalResourceId(),
                thumbNail.getFilePath(),
                originalResource.getFilePath(),
                originalResource.getFileTitle(),
                originalResource.getResolution(),
                originalResource.getFormat(),
                originalResource.getRegTime());
    }



    //수정
    // 원본 파일 제목 수정
    public void updateOrFileTitle(Long id, ResourceListDTO resourceListDTO, Account account) {

        OriginalResource originalResource = fileOriginRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다. id: " + id));
        String oldTitle = originalResource.getFileTitle();
        originalResource.setFileTitle(resourceListDTO.getFileTitle());
        String newTitle = resourceListDTO.getFileTitle();

        ActivityLog activityLog = ActivityLog.builder().account(account).activityDetail("원본 " +oldTitle + "에서 " + newTitle + "로 변경되었습니다.").dateTime(LocalDateTime.now()).build();
        activityLogRepository.save(activityLog);
        fileOriginRepository.save(originalResource);

    }

    // 인코딩 파일 제목 수정
    public void updateErFileTitle(Long id, EncodeListDTO encodeListDTO, Account account) {
        EncodedResource encodedResource = fileEncodedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다. id: " + id));
        String oldTitle = encodedResource.getFileTitle();
        encodedResource.setFileTitle(encodeListDTO.getFileTitle());
        String newTitle = encodeListDTO.getFileTitle();


        ActivityLog activityLog = ActivityLog.builder().account(account).activityDetail("인코딩 " + oldTitle + "에서 " + newTitle + "로 변경되었습니다.").dateTime(LocalDateTime.now()).build();
        activityLogRepository.save(activityLog);
        fileEncodedRepository.save(encodedResource); // 변경된 내용을 저장
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

    @Transactional
    //비활성화 된 파일 다시 활성화
    public void activationFile(Long id) {
        OriginalResource originalResource = fileOriginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다."));

        originalResource.setActive(true);
        fileOriginRepository.save(originalResource);

    }

    @Transactional
    // 파일 삭제 및 관련된 썸네일 삭제
    public void deactivationFile(Long id) {
        OriginalResource originalResource = fileOriginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 파일을 찾을 수 없습니다."));
        List<EncodedResource> encodedResources = fileEncodedRepository.findByOriginalResource(originalResource);

        // 리스트 내의 인코딩된 파일 삭제
        for (EncodedResource encodedResource : encodedResources) {
            deleteFileFromStorage(encodingLocation+encodedResource.getFileName());
        }

        originalResource.setActive(false);
        fileOriginRepository.save(originalResource);

        // 시퀀스 DB 삭제 (각 EncodedResource에 대해 개별적으로 삭제)
        for (EncodedResource encodedResource : encodedResources) {
            playlistSequenceRepository.deleteByEncodedResource(encodedResource);
        }

        // deviceEncodedMap DB 삭제 (각 EncodedResource ID에 대해 개별적으로 삭제)
        for (EncodedResource encodedResource : encodedResources) {
            deviceEncodeMapRepository.deleteByEncodedResourceId(encodedResource.getEncodedResourceId());
        }

        // 연관된 인코딩 파일 DB 삭제
        fileEncodedRepository.deleteByOriginalResource(originalResource);

        // 파일 크기 정보를 조회
        FileSize fileSize = fileSizeRepository.findByFileSizeId(1);

        if (fileSize != null) {
            long totalEncodedFileSize = encodedResources.stream()
                    .mapToLong(EncodedResource::getFileSize)
                    .sum();  // 모든 인코딩된 파일의 용량 합산

            // 리소스 타입에 따른 용량 처리
            if (originalResource.getResourceType() == ResourceType.IMAGE) {
                fileSize.setTotalEncodedImage(fileSize.getTotalEncodedImage() - totalEncodedFileSize);
            } else if (originalResource.getResourceType() == ResourceType.VIDEO) {
                fileSize.setTotalEncodedVideo(fileSize.getTotalEncodedVideo() - totalEncodedFileSize);
            }
        }}
        //인코딩 파일 삭제 관련 파일
        @Transactional
        public void deleteEncodedFile(Long id) {
            EncodedResource encodedResource = fileEncodedRepository.findByEncodedResourceId(id);

            // 인코딩된 파일 삭제
            deleteFileFromStorage(encodingLocation+encodedResource.getFileName());

            // 시퀀스 DB 삭제 (각 EncodedResource에 대해 개별적으로 삭제)
            playlistSequenceRepository.deleteByEncodedResource(encodedResource);


            // deviceEncodedMap DB 삭제 (각 EncodedResource ID에 대해 개별적으로 삭제)
            deviceEncodeMapRepository.deleteByEncodedResourceId(encodedResource.getEncodedResourceId());


            // 인코딩 파일 DB 삭제
            fileEncodedRepository.deleteById(id);


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





