package com.boot.ksis.controller.file;

import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.service.file.FileBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resourceList")
public class FileBoardController {

    private final FileBoardService fileBoardService;

    // 업로드된 파일 목록 조회
    @GetMapping
    public ResponseEntity<List<ResourceListDTO>> getAllFiles() {
        List<ResourceListDTO> files = fileBoardService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 업로드된 이미지 파일 목록 조회
    @GetMapping("/images")
    public ResponseEntity<List<ResourceListDTO>> getImageFiles() {
        List<ResourceListDTO> imageFiles = fileBoardService.getImageFiles();
        return ResponseEntity.ok(imageFiles);
    }


    // 업로드된 동영상 파일 목록 조회
    @GetMapping("/videos")
    public ResponseEntity<List<ResourceListDTO>> getVideoFiles() {
        List<ResourceListDTO> videoFiles = fileBoardService.getVideoFiles();
        return ResponseEntity.ok(videoFiles);
    }

    // 파일 제목 수정
    @PutMapping("/{originalResourceId}")
    public ResponseEntity<OriginalResource> updateFileTitle(@PathVariable Long originalResourceId, @RequestParam String newTitle) {
        Optional<OriginalResource> updatedResource = fileBoardService.updateFileTitle(originalResourceId, newTitle);
        return updatedResource
                .map(resource -> ResponseEntity.ok(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 파일 삭제
    @DeleteMapping("/{originalResource}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long originalResource) {
        fileBoardService.deleteFile(originalResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }
}
