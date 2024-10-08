package com.boot.ksis.controller.file;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.OriginResourceListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.service.file.FileBoardService;
import com.boot.ksis.service.file.FileEncodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resourceList")
public class FileBoardController {

    private final FileBoardService fileBoardService;
    private final FileEncodingService fileEncodingService;
    private final AccountRepository accountRepository;

    // 업로드된 원본 파일 목록 조회
    @GetMapping("/All/{originalResourceId}")
    public ResponseEntity<List<ResourceListDTO>> getAllFiles() {
        List<ResourceListDTO> files = fileBoardService.getAllFiles();
        return ResponseEntity.ok(files);
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/RsImages")
    public ResponseEntity<?> getRsImageFiles(Principal principal) {
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

//        if (!accountOptional.isPresent()) {
//            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
//        }

        // 역할 구분 없이 본인이 올린 이미지 파일 목록 조회
        List<ResourceListDTO> imageFiles = fileBoardService.getRsImageFiles(accountOptional);

        return ResponseEntity.ok(imageFiles);
    }

    // 업로드된 원본 동영상 파일 목록 조회
    @GetMapping("/RsVideos")
    public ResponseEntity<?> getVideoFiles(Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

        List<ResourceListDTO> videoFiles = fileBoardService.getRsVideoFiles(accountOptional);
        return ResponseEntity.ok(videoFiles);
    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/EcImages")
    public ResponseEntity<?> getEcImageFiles(Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

        // 역할 구분 없이 본인이 올린 이미지 파일 목록 조회
        List<EncodeListDTO> imageFiles = fileBoardService.getEcImageFiles(accountOptional);

        return ResponseEntity.ok(imageFiles);

    }

    // 업로드된 인코딩 동영상 파일 목록 조회
    @GetMapping("/EcVideos")
    public ResponseEntity<?> getEcVideoFiles(Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

        List<EncodeListDTO> videoFiles = fileBoardService.getEcVideoFiles(accountOptional);
        return ResponseEntity.ok(videoFiles);
    }

    
    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/files/{originalResourceId}")
    public ResponseEntity<ResourceListDTO> getResourceFiles(@PathVariable Long originalResourceId) {
        ResourceListDTO allFiles = fileBoardService.getResourceFiles(originalResourceId);
        return ResponseEntity.ok(allFiles);
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

    // 원본 파일 제목 수정
    @CustomAnnotation(activityDetail = "원본 파일 제목 수정")
    @PutMapping("/original/{originalResourceId}")
    public ResponseEntity<Void> updateOrFileTitle(
            @PathVariable Long originalResourceId,
            @RequestBody ResourceListDTO resourceListDTO) {

        fileBoardService.updateOrFileTitle(originalResourceId, resourceListDTO);
        return ResponseEntity.noContent().build();


    }

    // 인코딩 파일 제목 수정
    @CustomAnnotation(activityDetail = "인코딩 파일 제목 수정")
    @PutMapping("/encoded/{encodedResourceId}")
    public ResponseEntity<Void> updateFileTitle(
            @PathVariable Long encodedResourceId,
            @RequestBody EncodeListDTO encodeListDTO) {

        fileBoardService.updateErFileTitle(encodedResourceId, encodeListDTO);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 파일 삭제
    // 삭제하면 인코딩 파일, 썸네일 다 DB에서 삭제
    @CustomAnnotation(activityDetail = "원본 파일 삭제")
    @DeleteMapping("/original/{originalResource}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long originalResource) {
        fileBoardService.deleteFile(originalResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }

    //인코딩 파일 삭제, 인코딩 파일만 DB에서 삭제
    @CustomAnnotation(activityDetail = "인코딩 파일 삭제")
    @DeleteMapping("/encoded/{encodedResource}")
    public ResponseEntity<Void> deleteEncodedFile(@PathVariable Long encodedResource) {
        fileBoardService.deleteEncodedFile(encodedResource);
        return ResponseEntity.noContent().build();  // 삭제 후 성공 응답
    }

    // 인코딩 요청을 처리하는 엔드포인트
    @PostMapping("/img/encoding/{originalResourceId}")
    public ResponseEntity<String> imageEncodingBoard(
            @PathVariable("originalResourceId") Long originalResourceId,
            @RequestBody OriginResourceListDTO originResourceListDTO) {
        try {
            // 서비스 메서드 호출
            fileEncodingService.imageEncodingBoard(originalResourceId, originResourceListDTO);
            return ResponseEntity.ok("이미지 인코딩이 성공적으로 시작되었습니다 . ");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 인코딩 실패 : " + e.getMessage());
        }
    }

    // 인코딩 요청을 처리하는 엔드포인트
    @PostMapping("/video/encoding/{originalResourceId}")
    public ResponseEntity<String> videoEncodingBoard(
            @PathVariable("originalResourceId") Long originalResourceId,
            @RequestBody OriginResourceListDTO originResourceListDTO) {
        try {
            // 서비스 메서드 호출
            fileEncodingService.videoEncodingBoard(originalResourceId, originResourceListDTO);
            return ResponseEntity.ok("영상 인코딩이 성공적으로 시작되었습니다 . ");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("영상 인코딩 실패 : " + e.getMessage());
        }
    }

}
