package com.boot.ksis.controller.log;

import com.boot.ksis.dto.log.UploadLogDTO;
import com.boot.ksis.service.log.UploadLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UploadLogController {

    private final UploadLogService uploadLogService;

    @PostMapping("/upload-log")
    public ResponseEntity<Void> uploadLog(@RequestBody UploadLogDTO uploadLogDTO){

        uploadLogService.uploadLog(uploadLogDTO);

        return ResponseEntity.ok().build();
    }
}
