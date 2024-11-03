package com.boot.ksis.repository.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileOriginRepository  extends JpaRepository<OriginalResource, Long> {
    OriginalResource findByOriginalResourceId(Long originalResourceId);

    // 파일 이름으로 OriginalResource를 조회하는 메서드
    Optional<OriginalResource> findByFileName(String fileName);

    // 파일 제목으로 OriginalResource를 조회
    Optional<OriginalResource> findByFileTitle(String fileTitle);


    List<OriginalResource> findByAccount(Account account);

    //파일 상태로 조회하는 메서드
    List<OriginalResource> findByResourceStatusAndIsActive(ResourceStatus resourceStatus, Boolean isActive);

    //본인이 업로드 완료한 것 중 파일 타입으로 조회
    List<OriginalResource> findByAccountAndResourceStatusAndResourceTypeAndIsActiveOrderByRegTimeDesc(Account account, ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive);

    //본인이 업로드 완료한 것만 조회
    List<OriginalResource> findByAccountAndResourceStatusAndIsActiveOrderByRegTimeDesc(Account account, ResourceStatus resourceStatus, Boolean isActive);

    //전체 조회
    List<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveOrderByRegTimeDesc(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive);




    //관리자 페이징
    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCaseAndAccount(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, String fileTitle, Account accountId, Pageable pageable);

    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCaseAndAccount( ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, String resolution, Account accountId, Pageable pageable);

    @Query("SELECT r FROM OriginalResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus AND r.resourceType = :resourceType AND r.isActive = :isActive And r.account =:accountId")
    Page<OriginalResource> searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCaseAndAccount
            (@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, @Param("resourceType") ResourceType resourceType, @Param("isActive")Boolean isActive,  @Param("accountId") Account accountId, Pageable pageable);

    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveAndAccountContainingIgnoreCase( ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, Account accountId, Pageable pageable);

    //일반 유저 페이징
    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveAndFileTitleContainingIgnoreCase(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, String fileTitle, Pageable pageable);

    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveAndResolutionContainingIgnoreCase(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, String resolution, Pageable pageable);

    @Query("SELECT r FROM OriginalResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus AND r.resourceType = :resourceType AND r.isActive = :isActive")
    Page<OriginalResource> searchByRegTimeAndResourceStatusAndResourceTypeAndIsActiveContainingIgnoreCase(@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, @Param("resourceType") ResourceType resourceType, @Param("isActive")Boolean isActive, Pageable pageable);


    Page<OriginalResource> findByResourceStatusAndResourceTypeAndIsActive(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive, Pageable pageable);

    // 관리자용: 날짜 범위로 regTime을 조회
    Page<OriginalResource> findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActive(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            ResourceStatus resourceStatus,
            ResourceType resourceType,
            Boolean isActive,
            Pageable pageable);

    // 일반 사용자용: 날짜 범위로 regTime을 조회 + account 필터
    Page<OriginalResource> findByRegTimeBetweenAndResourceStatusAndResourceTypeAndIsActiveAndAccount(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            ResourceStatus resourceStatus,
            ResourceType resourceType,
            Boolean isActive,
            Account accountId,
            Pageable pageable);

}