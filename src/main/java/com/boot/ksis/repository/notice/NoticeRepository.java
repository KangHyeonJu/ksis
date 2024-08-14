package com.boot.ksis.repository.notice;

import com.boot.ksis.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 필요한 경우, 커스텀 쿼리 메소드 정의 가능
}