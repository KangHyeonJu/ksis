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
    Page<EncodedResource> findByFileTitleAndResourceStatusContainingIgnoreCaseAndOriginalResource_Account(String fileTitle, ResourceStatus resourceStatus, Account accountId, Pageable pageable);

    Page<EncodedResource> findByResolutionAndResourceStatusContainingIgnoreCaseAndOriginalResource_Account(String resolution, ResourceStatus resourceStatus, Account accountId, Pageable pageable);

    @Query("SELECT r FROM EncodedResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus And r.originalResource.account =:accountId")
    Page<EncodedResource> searchByRegTimeAndResourceStatusContainingIgnoreCaseAndOriginalResource_Account(@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, @Param("accountId") Account accountId, Pageable pageable);

    Page<EncodedResource> findByResourceStatusAndResourceTypeContainingIgnoreCaseAndOriginalResource_Account(ResourceStatus resourceStatus, ResourceType resourceType, Account accountId, Pageable pageable);

    //일반 유저 페이징
    Page<EncodedResource> findByFileTitleAndResourceStatusContainingIgnoreCase(String fileTitle, ResourceStatus resourceStatus, Pageable pageable);

    Page<EncodedResource> findByResolutionAndResourceStatusContainingIgnoreCase(String resolution, ResourceStatus resourceStatus, Pageable pageable);

    @Query("SELECT r FROM EncodedResource r WHERE CAST(r.regTime AS string) LIKE %:searchTerm% AND r.resourceStatus = :resourceStatus")
    Page<EncodedResource> searchByRegTimeAndResourceStatusContainingIgnoreCase(@Param("searchTerm") String searchTerm, @Param("resourceStatus") ResourceStatus resourceStatus, Pageable pageable);


    Page<EncodedResource> findByResourceStatusAndResourceTypeContainingIgnoreCase(ResourceStatus resourceStatus, ResourceType resourceType, Pageable pageable);
}
