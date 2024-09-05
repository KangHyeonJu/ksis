package com.boot.ksis.repository.log;

import com.boot.ksis.entity.Log.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
