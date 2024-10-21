package com.boot.ksis.controller.file;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.constant.Role;
import com.boot.ksis.dto.file.EncodeListDTO;
import com.boot.ksis.dto.file.OriginResourceListDTO;
import com.boot.ksis.dto.file.ResourceListDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.repository.account.AccountRepository;
import com.boot.ksis.service.file.FileBoardService;
import com.boot.ksis.service.file.FileEncodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
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
    @GetMapping("/active/RsImages")
    public ResponseEntity<?> getActiveRsImageFiles(Principal principal,
                                                   @RequestParam int page,
                                                   @RequestParam int size,
                                                   @RequestParam(required = false) String searchTerm,
                                                   @RequestParam(required = false) String searchCategory) {
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<ResourceListDTO> imageFiles;

        try {
            imageFiles = fileBoardService.getRsActiveImageFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: ", e);
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 조회 결과 반환
        return ResponseEntity.ok(imageFiles);
    }

    // 업로드된 원본 동영상 파일 목록 조회
    @GetMapping("/active/RsVideos")
    public ResponseEntity<?> getActiveVideoFiles(Principal principal,
                                                 @RequestParam int page,
                                                 @RequestParam int size,
                                                 @RequestParam(required = false) String searchTerm,
                                                 @RequestParam(required = false) String searchCategory) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<ResourceListDTO> videoFiles;

        try {
            videoFiles = fileBoardService.getRsActiveVideoFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: ", e);
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 조회 결과 반환
        return ResponseEntity.ok(videoFiles);
    }

    // 업로드된 비활성화 원본 이미지 파일 목록 조회
    @GetMapping("/deactivation/Images")
    public ResponseEntity<?> getDeactivationImageFiles(Principal principal,
                                                       @RequestParam int page,
                                                       @RequestParam int size,
                                                       @RequestParam(required = false) String searchTerm,
                                                       @RequestParam(required = false) String searchCategory) {
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<ResourceListDTO> imageFiles;

        try {
            imageFiles = fileBoardService.getDeactiveImageFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: ", e);
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 조회 결과 반환
        return ResponseEntity.ok(imageFiles);
    }

    // 업로드된 비활성화 원본 동영상 파일 목록 조회
    @GetMapping("/deactivation/Videos")
    public ResponseEntity<?> getDeactivationVideoFiles(Principal principal,
                                                       @RequestParam int page,
                                                       @RequestParam int size,
                                                       @RequestParam(required = false) String searchTerm,
                                                       @RequestParam(required = false) String searchCategory) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<ResourceListDTO> videoFiles;

        try {
            videoFiles = fileBoardService.getDeactiveVideoFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: ", e);
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 조회 결과 반환
        return ResponseEntity.ok(videoFiles);

    }

    // 업로드된 원본 이미지 파일 목록 조회
    @GetMapping("/EcImages")
    public ResponseEntity<?> getEcImageFiles(Principal principal,
                                             @RequestParam int page,
                                             @RequestParam int size,
                                             @RequestParam(required = false) String searchTerm,
                                             @RequestParam(required = false) String searchCategory) {

        // 인증 확인
        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<EncodeListDTO> imageFiles;

        try {
            imageFiles = fileBoardService.getEcActiveImageFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생: ", e);
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 조회 결과 반환
        return ResponseEntity.ok(imageFiles);
    }


    // 업로드된 인코딩 동영상 파일 목록 조회
    @GetMapping("/EcVideos")
    public ResponseEntity<?> getEcVideoFiles(Principal principal,
                                             @RequestParam int page,
                                             @RequestParam int size,
                                             @RequestParam(required = false) String searchTerm,
                                             @RequestParam(required = false) String searchCategory) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (!accountOptional.isPresent()) {
            return new ResponseEntity<>("계정 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }

        Account account = accountOptional.get();
        Role role = account.getRole();

        Page<EncodeListDTO> videoFiles;

        try {
            videoFiles = fileBoardService.getEcActiveVideoFiles(page, size, searchTerm, searchCategory, account, role);
        } catch (Exception e) {
            return new ResponseEntity<>("파일 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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
    @PutMapping("/original/{originalResourceId}")
    public ResponseEntity<?> updateOrFileTitle(
            @PathVariable Long originalResourceId,
            @RequestBody ResourceListDTO resourceListDTO, Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

        fileBoardService.updateOrFileTitle(originalResourceId, resourceListDTO, accountOptional);
        return ResponseEntity.noContent().build();


    }

    // 인코딩 파일 제목 수정
    @PutMapping("/encoded/{encodedResourceId}")
    public ResponseEntity<?> updateFileTitle(
            @PathVariable Long encodedResourceId,
            @RequestBody EncodeListDTO encodeListDTO, Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("사용자가 인증되지 않았습니다.", HttpStatus.UNAUTHORIZED);
        }

        String accountId = principal.getName();

        // Account 객체를 repository를 통해 조회
        Account accountOptional = accountRepository.findById(accountId).orElse(null);

        fileBoardService.updateErFileTitle(encodedResourceId, encodeListDTO, accountOptional);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 비활성화 한 원본 소스 다시 활성화
    @CustomAnnotation(activityDetail = "원본 파일 활성화")
    @PostMapping("/active/{originalResource}")
    public ResponseEntity<Void> activeFile(@PathVariable Long originalResource) {
        fileBoardService.activationFile(originalResource);
        return ResponseEntity.noContent().build();  // 활성화 후 성공 응답
    }

    // 파일 삭제
    // 삭제하면 인코딩 파일, 썸네일 다 DB에서 삭제
    @CustomAnnotation(activityDetail = "원본 파일 비활성화")
    @PostMapping("/deactivation/{originalResource}")
    public ResponseEntity<Void> deactivationFile(@PathVariable Long originalResource) {
        fileBoardService.deactivationFile(originalResource);
        return ResponseEntity.noContent().build();  // 비활성화 후 성공 응답
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
    public ResponseEntity<?> imageEncodingBoard(
            @PathVariable("originalResourceId") Long originalResourceId,
            @RequestBody OriginResourceListDTO originResourceListDTO) {

        try{
            if(fileEncodingService.checkResolution(originResourceListDTO)){
                // 서비스 메서드 호출
                fileEncodingService.imageEncodingBoard(originalResourceId, originResourceListDTO);
                return ResponseEntity.ok("이미지 인코딩이 성공적으로 시작되었습니다 .");

            }else{
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("동일한 해상도와 포멧이 존재합니다 .");
            }
        } catch (IOException e) {
            fileEncodingService.encodingNotification(originResourceListDTO, "인코딩 실패");
            fileEncodingService.encodingLog(originResourceListDTO, "인코딩 실패");
            return ResponseEntity.status(500).body("이미지 인코딩 실패 : " + e.getMessage());
        }
    }
    // 인코딩 요청을 처리하는 엔드포인트
    @PostMapping("/video/encoding/{originalResourceId}")
    public ResponseEntity<String> videoEncodingBoard(
            @PathVariable("originalResourceId") Long originalResourceId,
            @RequestBody OriginResourceListDTO originResourceListDTO) {
        try {
            if(fileEncodingService.checkResolution(originResourceListDTO)){
                fileEncodingService.videoEncodingBoard(originalResourceId, originResourceListDTO);
                return ResponseEntity.ok("영상 인코딩이 성공적으로 시작되었습니다 .");

            }else{
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("동일한 해상도와 포멧이 존재합니다 .");
            }
        } catch (IOException e) {
            fileEncodingService.encodingNotification(originResourceListDTO, "인코딩 실패");
            fileEncodingService.encodingLog(originResourceListDTO, "인코딩 실패");
            return ResponseEntity.status(500).body("영상 인코딩 실패 : " + e.getMessage());
        }
    }

}
