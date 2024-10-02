package com.boot.ksis.repository.log;

import com.boot.ksis.entity.Log.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // accountId로 검색 (부분 검색 가능)
    Page<ActivityLog> findByAccount_AccountIdContainingIgnoreCase(String accountId, Pageable pageable);

    // activityDetail로 검색 (부분 검색 가능)
    Page<ActivityLog> findByActivityDetailContainingIgnoreCase(String activityDetail, Pageable pageable);

    // accountId로 검색 + 날짜 범위로 필터링
    Page<ActivityLog> findByAccount_AccountIdContainingIgnoreCaseAndDateTimeBetween(String accountId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // activityDetail로 검색 + 날짜 범위로 필터링
    Page<ActivityLog> findByActivityDetailContainingIgnoreCaseAndDateTimeBetween(String activityDetail, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // accountId로 검색 + 시작 시간 이후 데이터 필터링
    Page<ActivityLog> findByAccount_AccountIdContainingIgnoreCaseAndDateTimeAfter(String accountId, LocalDateTime startTime, Pageable pageable);

    // activityDetail로 검색 + 시작 시간 이후 데이터 필터링
    Page<ActivityLog> findByActivityDetailContainingIgnoreCaseAndDateTimeAfter(String activityDetail, LocalDateTime startTime, Pageable pageable);

    // accountId로 검색 + 종료 시간 이전 데이터 필터링
    Page<ActivityLog> findByAccount_AccountIdContainingIgnoreCaseAndDateTimeBefore(String accountId, LocalDateTime endTime, Pageable pageable);

    // activityDetail로 검색 + 종료 시간 이전 데이터 필터링
    Page<ActivityLog> findByActivityDetailContainingIgnoreCaseAndDateTimeBefore(String activityDetail, LocalDateTime endTime, Pageable pageable);
}
