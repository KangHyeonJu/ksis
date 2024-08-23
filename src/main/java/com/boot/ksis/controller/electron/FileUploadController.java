package com.boot.ksis.controller.electron;

import com.boot.ksis.dto.upload.OriginalResourceDTO;
import com.boot.ksis.service.upload.OriginalResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${uploadLocation}")
    String uploadLocation;

    @Value("${thumbnailsLocation}")
    String thumbnailsLocation;

    // 파일이 저장되는 경로
    @PostMapping("/filedatasave")
    public ResponseEntity<List<OriginalResourceDTO>> uploadFile(
            @RequestPart("dtos") List<OriginalResourceDTO> originalResourceDTOS,
            @RequestPart("files") List<MultipartFile> files){
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
    ){
        try{
            // 상태를 COMPLETED로 업데이트
            originalResourceService.chunkUpload(chunk, fileName, chunkIndex, totalChunks);
            return ResponseEntity.ok("청크 업로드 성공");
        } catch (Exception e){
            logger.error("청크 파일 업로드 API", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
