package com.boot.ksis.controller.file;

import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.dto.file.ResourceThumbDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.entity.EncodedResource;
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

    // 업로드된 원본 파일 목록 조회
    @GetMapping
    public ResponseEntity<List<ResourceListDTO>> getAllFiles() {
        List<ResourceListDTO> files = fileBoardService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/RsImages")
    public ResponseEntity<List<ResourceListDTO>> getImageFiles() {
        List<ResourceListDTO> imageFiles = fileBoardService.getRsImageFiles();
        return ResponseEntity.ok(imageFiles);
    }


    // 업로드된 원본 동영상 파일 목록 조회
    @GetMapping("/RsVideos")
    public ResponseEntity<List<ResourceListDTO>> getVideoFiles() {
        List<ResourceListDTO> videoFiles = fileBoardService.getRsVideoFiles();
        return ResponseEntity.ok(videoFiles);
    }
    // 원본 특정 파일 상세조회 
    @GetMapping("/original/{originalResourceId}")
    public ResponseEntity<List<EncodeListDTO>> getResourceDtl (@PathVariable Long originalResourceId) {
        //  상세 조회 서비스 호출
        List<EncodeListDTO> encodeListDTO = fileBoardService.getResourceDtl(originalResourceId);
        return ResponseEntity.ok(encodeListDTO); //원본 특정 파일 상세조회값 반환
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/EcImages")
    public ResponseEntity<List<EncodeListDTO>> getEcImageFiles() {
        List<EncodeListDTO> imageFiles = fileBoardService.getEcImageFiles();
        return ResponseEntity.ok(imageFiles);
    }
    // 업로드된 인코딩 동영상 파일 목록 조회
    @GetMapping("/EcVideos")
    public ResponseEntity<List<EncodeListDTO>> getEcVideoFiles() {
        List<EncodeListDTO> videoFiles = fileBoardService.getEcVideoFiles();
        return ResponseEntity.ok(videoFiles);
    }

    // 원본 파일 제목 수정
    @PutMapping("/original/{originalResourceId}")
    public ResponseEntity<OriginalResource> updateOrFileTitle(@PathVariable Long originalResourceId, @RequestParam String newTitle) {
        Optional<OriginalResource> updatedRsResource = fileBoardService.updateOrFileTitle(originalResourceId, newTitle);
        return updatedRsResource
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 인코딩 파일 제목 수정
    @PutMapping("/encoded/{encodedResourceId}")
    public ResponseEntity<EncodedResource> updateErFileTitle(@PathVariable Long encodedResourceId, @RequestParam String newTitle) {
        Optional<EncodedResource> updatedEcResource = fileBoardService.updateErFileTitle(encodedResourceId, newTitle);
        return updatedEcResource
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 파일 삭제
    // 삭제하면 인코딩 파일, 썸네일 다 DB에서 삭제
    @DeleteMapping("/original/{originalResource}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long originalResource) {
        fileBoardService.deleteFile(originalResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }

    @DeleteMapping("/encoded/{encodedResource}")
    public ResponseEntity<Void> deleteEncodedFile(@PathVariable Long encodedResource) {
        fileBoardService.deleteFile(encodedResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }
}
