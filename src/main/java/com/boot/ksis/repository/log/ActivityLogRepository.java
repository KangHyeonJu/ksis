package com.boot.ksis.repository.log;

import com.boot.ksis.entity.Log.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
