package com.boot.ksis.controller.file;

import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.service.file.FileBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class FileBoardController {

    private final FileBoardService fileBoardService;

    // 업로드된 파일 목록 조회
    @GetMapping("/resourceList")
    public ResponseEntity<List<OriginalResource>> getAllFiles() {
        List<OriginalResource> files = fileBoardService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 업로드된 이미지 파일 목록 조회
    @GetMapping("/resourceList/images")
    public ResponseEntity<List<OriginalResource>> getImageFiles() {
        List<OriginalResource> imageFiles = fileBoardService.getImageFiles();
        return ResponseEntity.ok(imageFiles);
    }

    // 업로드된 동영상 파일 목록 조회
    @GetMapping("/resourceList/videos")
    public ResponseEntity<List<OriginalResource>> getVideoFiles() {
        List<OriginalResource> videoFiles = fileBoardService.getVideoFiles();
        return ResponseEntity.ok(videoFiles);
    }
}
