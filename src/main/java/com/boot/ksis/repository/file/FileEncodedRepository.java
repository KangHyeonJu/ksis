package com.boot.ksis.repository.file;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


@Repository
public interface FileEncodedRepository extends JpaRepository<EncodedResource, Long> {
    // 중복 해상도 방지를 위한 메소드
    EncodedResource findByOriginalResourceAndResolutionAndFormat(OriginalResource originalResource, String resolution, String format);


    EncodedResource findByEncodedResourceId(Long encodedResourceId);

    void deleteByOriginalResource(OriginalResource originalResource);

    List<EncodedResource> findByOriginalResource(OriginalResource originalResource);

    List<EncodedResource> findByOriginalResourceAndResourceStatusOrderByRegTimeDesc(OriginalResource originalResource, ResourceStatus resourceStatus);

    List<EncodedResource> findByOriginalResourceInAndResourceStatusAndResourceTypeOrderByRegTimeDesc(List<OriginalResource> originalResource, ResourceStatus resourceStatus, ResourceType resourceType);

    //관리자 페이징
    Page<EncodedResource> findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCaseAndOriginalResource_Account( ResourceStatus resourceStatus, ResourceType resourceType, String fileTitle, Account accountId, Pageable pageable);

    Page<EncodedResource> findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCaseAndOriginalResource_Account( ResourceStatus resourceStatus, ResourceType resourceType, String resolution, Account accountId, Pageable pageable);

    @Query("SELECT r FROM EncodedResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus AND r.resourceType = :resourceType And r.originalResource.account =:accountId")
    Page<EncodedResource> searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCaseAndOriginalResource_Account(@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, @Param("resourceType") ResourceType resourceType, @Param("accountId") Account accountId, Pageable pageable);

    Page<EncodedResource> findByResourceStatusAndResourceTypeAndOriginalResource_AccountContainingIgnoreCase( ResourceStatus resourceStatus, ResourceType resourceType, Account accountId, Pageable pageable);

    //일반 유저 페이징
    Page<EncodedResource> findByResourceStatusAndResourceTypeAndFileTitleContainingIgnoreCase(ResourceStatus resourceStatus, ResourceType resourceType, String fileTitle, Pageable pageable);

    Page<EncodedResource> findByResourceStatusAndResourceTypeAndResolutionContainingIgnoreCase(ResourceStatus resourceStatus, ResourceType resourceType, String resolution, Pageable pageable);

    @Query("SELECT r FROM EncodedResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus AND r.resourceType = :resourceType")
    Page<EncodedResource> searchByRegTimeAndResourceStatusAndResourceTypeContainingIgnoreCase(@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, @Param("resourceType") ResourceType resourceType, Pageable pageable);


    Page<EncodedResource> findByResourceStatusAndResourceType(ResourceStatus resourceStatus, ResourceType resourceType, Pageable pageable);

    //상세조회 페이징
    Page<EncodedResource> findByOriginalResourceAndResourceStatusAndResourceType(
            OriginalResource originalResource, ResourceStatus resourceStatus, ResourceType resourceType, Pageable pageable);

}
