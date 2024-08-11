package com.boot.ksis.repository.notice;

import com.boot.ksis.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 추가적인 쿼리 메소드 정의가 필요하면 여기에 작성
}
