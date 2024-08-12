package com.boot.ksis.repository.upload;

import com.boot.ksis.entity.OriginalResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalResourceRepository extends JpaRepository<OriginalResource, Long> {
    // 제목으로 OriginalResource를 찾는 메서드 추가
    OriginalResource findByFileTitle(String fileTitle);
}
