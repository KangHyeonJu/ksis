package com.boot.ksis.repository.notice;

import com.boot.ksis.constant.Role;
import com.boot.ksis.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 특정 공지에 해당하는 매핑 정보를 삭제하는 메소드
    void deleteByNoticeId(Long noticeId);

    // Account 엔터티의 accountId로 공지 조회
    List<Notice> findByAccount_AccountIdAndIsActiveOrderByRegTimeDesc(String accountId, boolean isActive);

    List<Notice> findByAccount_RoleAndIsActiveOrderByRegTimeDesc(Role role, boolean isActive);

    List<Notice> findByIsActiveOrderByRegTimeDesc(boolean isActive);

    Optional<Notice> findByNoticeIdAndIsActiveOrderByRegTimeDesc(Long noticeId, boolean isActive);
}