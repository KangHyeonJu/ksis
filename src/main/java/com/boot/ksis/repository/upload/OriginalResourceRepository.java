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
public interface OriginalResourceRepository extends JpaRepository<OriginalResource, Long> {

    // 파일 이름으로 OriginalResource를 조회하는 메서드
    Optional<OriginalResource> findByFileName(String fileName);

    // 파일 제목으로 OriginalResource를 조회
    Optional<OriginalResource> findByFileTitle(String fileTitle);

    // 파일 타입(이미지, 영상) 조회
    List<OriginalResource> findByResourceType(ResourceType resourceType);

    //파일 삭제
    OriginalResource findByOriginalResourceId(Long originalResourceId);

    List<OriginalResource> findByAccount(Account account);

    //파일 상태로 조회하는 메서드
    List<OriginalResource> findByResourceStatus(ResourceStatus resourceStatus);

    //파일 상태 조회 와 파일 타입으로 조회
    List<OriginalResource> findByResourceStatusAndResourceType(ResourceStatus resourceStatus, ResourceType resourceType);

    //본인것만 조회
    List<OriginalResource> findByCreatedBy(String accountId);

    //본인이 업로드 완료한 것 중 파일 타입으로 조회
    List<OriginalResource> findByAccountAndResourceStatusAndResourceTypeOrderByRegTimeDesc(Account accountId, ResourceStatus resourceStatus, ResourceType resourceType);

    //본인이 업로드 완료한 것만 조회
    List<OriginalResource> findByAccountAndResourceStatusOrderByRegTimeDesc(Account accountId, ResourceStatus resourceStatus);

}
