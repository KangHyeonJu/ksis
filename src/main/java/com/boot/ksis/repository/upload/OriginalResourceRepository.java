package com.boot.ksis.repository.upload;

import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OriginalResourceRepository extends JpaRepository<OriginalResource, Long> {
    // 파일 이름으로 OriginalResource를 조회하는 메서드
    Optional<OriginalResource> findByFileName(String fileName);

    // 파일 타입(이미지, 영상) 조회
    List<OriginalResource> findByResourceType(ResourceType resourceType);

    //파일 삭제
    OriginalResource findByOriginalResourceId(Long originalResourceId);

}
