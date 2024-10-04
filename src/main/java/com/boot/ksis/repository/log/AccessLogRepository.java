package com.boot.ksis.repository.log;

import com.boot.ksis.constant.Category;
import com.boot.ksis.entity.Log.AccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    Page<AccessLog> findByAccount_AccountIdContainingIgnoreCase(String accountId, Pageable pageable);
    Page<AccessLog> findByCategory(Category category, Pageable pageable);

    Page<AccessLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBetween(String accountId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<AccessLog> findByCategoryAndRegTimeBetween(Category category, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    Page<AccessLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeAfter(String accountId, LocalDateTime startTime, Pageable pageable);
    Page<AccessLog> findByCategoryAndRegTimeAfter(Category category, LocalDateTime startTime, Pageable pageable);

    Page<AccessLog> findByAccount_AccountIdContainingIgnoreCaseAndRegTimeBefore(String accountId, LocalDateTime endTime, Pageable pageable);
    Page<AccessLog> findByCategoryAndRegTimeBefore(Category category, LocalDateTime endTime, Pageable pageable);

}
