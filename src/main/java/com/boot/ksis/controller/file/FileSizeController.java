package com.boot.ksis.controller.file;

import com.boot.ksis.entity.FileSize;
import com.boot.ksis.service.file.FileSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // React 애플리케이션의 주소
@RequestMapping("/api/filesize")
public class FileSizeController {

    @Autowired
    private FileSizeService fileSizeService;

    @GetMapping
    public ResponseEntity<FileSize> getFileSize() {
        FileSize fileSize = fileSizeService.getFileSize();
        return ResponseEntity.ok(fileSize);
    }

    @PutMapping
    public ResponseEntity<FileSize> updateFileSize(@RequestBody FileSize fileSize) {
        FileSize updatedFileSize = fileSizeService.updateFileSize(fileSize);
        return ResponseEntity.ok(updatedFileSize);
    }
}