package com.boot.ksis.controller.file;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.file.FileSizeDTO;
import com.boot.ksis.service.file.FileSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/filesize")
@RequiredArgsConstructor
public class FileSizeController {

    private final FileSizeService fileSizeService;

    // 현재 파일 크기 설정 조회
    @GetMapping
    public ResponseEntity<FileSizeDTO> getFileSize() {
        FileSizeDTO fileSizeDTO = fileSizeService.getFileSize();
        return ResponseEntity.ok(fileSizeDTO);
    }

    // 파일 크기 설정 업데이트
    @CustomAnnotation(activityDetail = "파일 최대 용량 설정")
    @PutMapping
    public ResponseEntity<FileSizeDTO> updateFileSize(@RequestBody FileSizeDTO fileSizeDTO) {
        FileSizeDTO updatedFileSizeDTO = fileSizeService.updateFileSize(fileSizeDTO);
        return ResponseEntity.ok(updatedFileSizeDTO);
    }
}
