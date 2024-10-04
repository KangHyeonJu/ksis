package com.boot.ksis.controller.log;

import com.boot.ksis.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogController {
    private final LogService logService;

    @GetMapping("/access")
    public ResponseEntity<?> accessLogList(){
        return new ResponseEntity<>(logService.getAccessLogList(), HttpStatus.OK);
    }

    @GetMapping("/activity")
    public ResponseEntity<?> activityLogList(){
        return new ResponseEntity<>(logService.getActivityLogList(), HttpStatus.OK);
    }

    @GetMapping("/upload")
    public ResponseEntity<?> uploadLogList(){
        return new ResponseEntity<>(logService.getUploadLogList(), HttpStatus.OK);
    }
}
