package com.boot.ksis.controller.log;

import com.boot.ksis.dto.log.AccessLogDTO;
import com.boot.ksis.service.log.AccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccessLogController {
    private final AccessLogService accessLogService;

    @PostMapping("/access-log")
    public ResponseEntity<Void> logAccess(@RequestBody AccessLogDTO accessLogDto) {
        System.out.println("received DTO : " + accessLogDto);
        accessLogService.saveAccessLog(accessLogDto);
        return ResponseEntity.ok().build();
    }
}
