package com.boot.ksis.repository.notice;

import com.boot.ksis.entity.Notice;
import org.hibernate.sql.Delete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 특정 공지에 해당하는 매핑 정보를 삭제하는 메소드
    void deleteByNoticeId(Long noticeId);
}