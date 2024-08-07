package com.boot.ksis.controller;

import com.boot.ksis.entity.FileSize;
import com.boot.ksis.service.FileSizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
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
