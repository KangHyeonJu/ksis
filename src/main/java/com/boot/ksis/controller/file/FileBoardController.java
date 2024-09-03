package com.boot.ksis.controller.file;

import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.dto.file.ResourceThumbDTO;
import com.boot.ksis.dto.notice.NoticeDTO;
import com.boot.ksis.dto.upload.EncodingRequestDTO;
import com.boot.ksis.entity.EncodedResource;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.service.file.FileBoardService;
import com.boot.ksis.service.upload.EncodedResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resourceList")
public class FileBoardController {

    private final FileBoardService fileBoardService;
    private final EncodedResourceService encodedResourceService;

    // 업로드된 원본 파일 목록 조회
    @GetMapping("/All/{originalResourceId}")
    public ResponseEntity<List<ResourceListDTO>> getAllFiles() {
        List<ResourceListDTO> files = fileBoardService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/RsImages")
    public ResponseEntity<List<ResourceListDTO>> getRsImageFiles() {
        List<ResourceListDTO> imageFiles = fileBoardService.getRsImageFiles();
        return ResponseEntity.ok(imageFiles);
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/RsImages/{originalResourceId}")
    public ResponseEntity<ResourceListDTO> getImageFiles(@PathVariable Long originalResourceId) {
        ResourceListDTO imageFiles = fileBoardService.getImageFiles(originalResourceId);
        return ResponseEntity.ok(imageFiles);
    }


    // 업로드된 원본 동영상 파일 목록 조회
    @GetMapping("/RsVideos")
    public ResponseEntity<List<ResourceListDTO>> getVideoFiles() {
        List<ResourceListDTO> videoFiles = fileBoardService.getRsVideoFiles();
        return ResponseEntity.ok(videoFiles);
    }
    // 원본 특정 파일 상세조회 
    @GetMapping("/Img/{originalResourceId}")
    public ResponseEntity<List<EncodeListDTO>> getResourceImgDtl (@PathVariable Long originalResourceId) {
        //  상세 조회 서비스 호출
        List<EncodeListDTO> encodeListDTO = fileBoardService.getResourceImgDtl(originalResourceId);
        return ResponseEntity.ok(encodeListDTO); //원본 특정 파일 상세조회값 반환
    }
    // 원본 특정 파일 상세조회
    @GetMapping("/Video/{originalResourceId}")
    public ResponseEntity<List<EncodeListDTO>> getResourceVideoDtl (@PathVariable Long originalResourceId) {
        //  상세 조회 서비스 호출
        List<EncodeListDTO> encodeListDTO = fileBoardService.getResourceVideoDtl(originalResourceId);
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
    public ResponseEntity<Void> updateFileTitle(
            @PathVariable Long encodedResourceId,
            @RequestBody EncodeListDTO encodeListDTO) {

        fileBoardService.updateErFileTitle(encodedResourceId, encodeListDTO);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 파일 삭제
    // 삭제하면 인코딩 파일, 썸네일 다 DB에서 삭제
    @DeleteMapping("/original/{originalResource}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long originalResource) {
        fileBoardService.deleteFile(originalResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }

    //인코딩 파일 삭제, 인코딩 파일만 DB에서 삭제
    @DeleteMapping("/encoded/{encodedResource}")
    public ResponseEntity<Void> deleteEncodedFile(@PathVariable Long encodedResource) {
        fileBoardService.deleteEncodedFile(encodedResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }

    /*// 인코딩 요청을 처리하는 엔드포인트
    @PostMapping("/encoding/{originalResourceId}")
    public ResponseEntity<String> encodeByOriginalResourceId(
            @PathVariable Long originalResourceId,
            @RequestBody Map<String, EncodingRequestDTO> encodings) {

        try {
            // 인코딩 서비스 메서드 호출
            encodedResourceService.encodeByOriginalResourceId(originalResourceId, encodings);
            return ResponseEntity.ok("인코딩을 성공적으로 다음 아이디를 통해 시작했습니다.: " + originalResourceId);
        } catch (IllegalArgumentException e) {
            // 원본 리소스를 찾지 못한 경우 처리
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during encoding: " + e.getMessage());
        }
    }*/

}
