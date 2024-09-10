package com.boot.ksis.repository.log;

import com.boot.ksis.entity.Log.UploadLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadLogRepository extends JpaRepository<UploadLog, Long> {
}
