package com.boot.ksis.controller.electron;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class AppDownloadController {

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile() {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + "era-project Setup 0.1.0.exe");
            byte[] fileData = Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"era-project Setup 0.1.0.exe\"")
                    .body(fileData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
