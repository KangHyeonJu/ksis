package com.boot.ksis.repository.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncodedResourceRepository extends JpaRepository<EncodedResource, Long> {
    // 파일 이름으로 EncodedResource 조회하는 메서드
    Optional<EncodedResource> findByFileName(String fileName);

    EncodedResource findByEncodedResourceId(Long encodedResourceId);

    void deleteByOriginalResource(OriginalResource originalResource);
    
    List<EncodedResource> findByOriginalResource(OriginalResource originalResource);

    // 파일 타입(이미지, 영상) 조회
    List<EncodedResource> findByResourceType(ResourceType resourceType);

    //본인것만 조회
    List<EncodedResource> findByCreatedBy(String accountId);

    //파일 상태 , 파일 타입 조회
    List<EncodedResource> findByResourceStatusAndResourceType(ResourceStatus resourceStatus, ResourceType resourceType);

    //본인이 업로드 완료한 것 중 파일 타입으로 조회
    List<EncodedResource> findByOriginalResourceInAndResourceStatusAndResourceTypeOrderByRegTimeDesc(List<OriginalResource> originalResources, ResourceStatus resourceStatus, ResourceType resourceType);
}
