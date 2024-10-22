package com.boot.ksis.repository.upload;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OriginalResourceRepository extends JpaRepository<OriginalResource, Long> {
    OriginalResource findByOriginalResourceId(Long originalResourceId);

    // 파일 이름으로 OriginalResource를 조회하는 메서드
    Optional<OriginalResource> findByFileName(String fileName);

    // 파일 제목으로 OriginalResource를 조회
    Optional<OriginalResource> findByFileTitle(String fileTitle);

    List<OriginalResource> findByAccount(Account account);

}
