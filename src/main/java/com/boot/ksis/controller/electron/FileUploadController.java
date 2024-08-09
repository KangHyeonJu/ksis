package com.boot.ksis.controller.electron;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    private final String UPLOAD_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\uploads\\";

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("index") int index,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("filename") String filename) {
        try {
            // 청크 파일 저장
            String chunkFileName = filename + "-chunk-" + index;
            File chunkFile = new File(UPLOAD_DIR + chunkFileName);
            chunk.transferTo(chunkFile);

            // 모든 청크가 업로드 되었는지 확인
            if (index == totalChunks - 1) {
                mergeChunks(filename, totalChunks);
            }
            return ResponseEntity.ok("Chunk " + index + " uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving chunk");
        }
    }

    private void mergeChunks(String filename, int totalChunks) throws IOException {
        File mergedFile = new File(UPLOAD_DIR + filename);
        try (FileOutputStream fos = new FileOutputStream(mergedFile)) {
            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(UPLOAD_DIR + filename + "-chunk-" + i);
                Files.copy(chunkFile.toPath(), fos);
                chunkFile.delete();
            }
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        try {
            Path filePath = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.write(filePath, file.getBytes());
            return ResponseEntity.ok("File uploaded and saved successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving file");
        }
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("No files uploaded");
        }

        try {
            for (MultipartFile file : files) {
                Path filePath = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
                Files.write(filePath, file.getBytes());
            }
            return ResponseEntity.ok("All files uploaded and saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading files.");
        }
    }
}
