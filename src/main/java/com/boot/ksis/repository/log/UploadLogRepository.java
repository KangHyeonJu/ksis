package com.boot.ksis.repository.log;

import com.boot.ksis.entity.Log.UploadLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface UploadLogRepository extends JpaRepository<UploadLog, Long> {

    Page<UploadLog> findByAccount_AccountIdContainingIgnoreCase(String accountId, Pageable pageable);

    Page<UploadLog> findByMessageContainingIgnoreCase(String message, Pageable pageable);


    // Account ID로 검색 (부분 검색 가능)
    Page<UploadLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBetween(String accountId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 메시지로 검색 (부분 검색 가능)
    Page<UploadLog> findByMessageContainingIgnoreCaseAndRegTimeBetween(String message, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 특정 시간 이후의 로그 조회
    Page<UploadLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeAfter(String accountId, LocalDateTime startTime, Pageable pageable);
    Page<UploadLog> findByMessageContainingIgnoreCaseAndRegTimeAfter(String message, LocalDateTime startTime, Pageable pageable);

    // 특정 시간 이전의 로그 조회
    Page<UploadLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBefore(String accountId, LocalDateTime endTime, Pageable pageable);
    Page<UploadLog> findByMessageContainingIgnoreCaseAndRegTimeBefore(String message, LocalDateTime endTime, Pageable pageable);
}
