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


    List<OriginalResource> findByAccount(Account account);

    //파일 상태로 조회하는 메서드
    List<OriginalResource> findByResourceStatusAndIsActive(ResourceStatus resourceStatus, Boolean isActive);

    //본인것만 조회
    List<OriginalResource> findByCreatedBy(String accountId);

    //본인이 업로드 완료한 것 중 파일 타입으로 조회
    List<OriginalResource> findByAccountAndResourceStatusAndResourceTypeAndIsActiveOrderByRegTimeDesc(Account accountId, ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive);

    //본인이 업로드 완료한 것만 조회
    List<OriginalResource> findByAccountAndResourceStatusAndIsActiveOrderByRegTimeDesc(Account accountId, ResourceStatus resourceStatus, Boolean isActive);

    //관리자 조회
    List<OriginalResource> findByResourceStatusAndResourceTypeAndIsActiveOrderByRegTimeDesc(ResourceStatus resourceStatus, ResourceType resourceType, Boolean isActive);
}
