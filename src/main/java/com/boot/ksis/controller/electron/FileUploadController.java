package com.boot.ksis.controller.electron;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.boot.ksis.service.upload.OriginalResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileUploadController {

    private static final Logger logger = LogManager.getLogger(FileUploadController.class);

    private final OriginalResourceService originalResourceService;

    // 파일이 저장되는 경로
    @CustomAnnotation(activityDetail = "파일 업로드")
    @PostMapping("/filedatasave/{accountId}")
    public ResponseEntity<List<OriginalResourceDTO>> uploadFile(
            @RequestPart("dtos") List<OriginalResourceDTO> originalResourceDTOS,
            @RequestPart("files") List<MultipartFile> files, @PathVariable("accountId") String accountId) {
        try {
            // 서비스에서 파일 저장 및 데이터베이스 처리
            List<OriginalResourceDTO> returnDTO = originalResourceService.saveToDatabase(originalResourceDTOS, files);
            return ResponseEntity.ok(returnDTO);
        } catch (Exception e) {
            logger.error("파일 데이터 저장 API", e); // 오류 로그 기록
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/upload/chunk")
    public ResponseEntity<String> uploadChunk(
            @RequestParam("file") MultipartFile chunk,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks
    ) {
        try {
            // 청크 업로드
            originalResourceService.chunkUpload(chunk, fileName, chunkIndex, totalChunks);
            return ResponseEntity.ok("청크 업로드 성공");
        } catch (Exception e) {
            logger.error("청크 파일 업로드 API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // 업로드 중 파일 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestBody Map<String, String> request){

        originalResourceService.deleteFile(request);

        return ResponseEntity.ok("파일 삭제 성공");
    }

    // 파일 제목 중복 검증
    @PostMapping("/title/verification")
    public ResponseEntity<String> fileTitleVerification(@RequestBody Map<String, String> request){
        String title = request.get("title");

        String uniqueTitle = originalResourceService.titleVerification(title);

        return ResponseEntity.ok(uniqueTitle);
    }

}
