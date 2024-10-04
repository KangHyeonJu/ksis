package com.boot.ksis.controller.log;

import com.boot.ksis.dto.log.LogDTO;
import com.boot.ksis.entity.Log.UploadLog;
import com.boot.ksis.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final LogService logService;

    @GetMapping("/access")
    public ResponseEntity<?> accessLogList(@RequestParam int page,
                                           @RequestParam int size,
                                           @RequestParam(required = false) String searchTerm,
                                           @RequestParam(required = false) String searchCategory,
                                           @RequestParam(required = false) String startTime,
                                           @RequestParam(required = false) String endTime){

        Page<LogDTO> logs = logService.getAccessLogList(page, size, searchTerm, searchCategory, startTime, endTime);

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> activityLogList( @RequestParam int page,
                                              @RequestParam int size,
                                              @RequestParam(required = false) String searchTerm,
                                              @RequestParam(required = false) String searchCategory,
                                              @RequestParam(required = false) String startTime,
                                              @RequestParam(required = false) String endTime){

        Page<LogDTO> logs = logService.getActivityLogList(page, size, searchTerm, searchCategory, startTime, endTime);

        return ResponseEntity.ok(logs);
    }

    @GetMapping("/upload")
    public ResponseEntity<?> uploadLogList( @RequestParam int page,
                                            @RequestParam int size,
                                            @RequestParam(required = false) String searchTerm,
                                            @RequestParam(required = false) String searchCategory,
                                            @RequestParam(required = false) String startTime,
                                            @RequestParam(required = false) String endTime){

        Page<LogDTO> logs = logService.getUploadLogList(page, size, searchTerm, searchCategory, startTime, endTime);

        return ResponseEntity.ok(logs);
    }
}
