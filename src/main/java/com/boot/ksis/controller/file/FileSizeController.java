package com.boot.ksis.controller.file;

import com.boot.ksis.dto.file.FileSizeDTO;
import com.boot.ksis.service.file.FileSizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소
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
    @PutMapping
    public ResponseEntity<FileSizeDTO> updateFileSize(@RequestBody FileSizeDTO fileSizeDTO) {
        FileSizeDTO updatedFileSizeDTO = fileSizeService.updateFileSize(fileSizeDTO);
        return ResponseEntity.ok(updatedFileSizeDTO);
    }
}
