package com.boot.ksis.controller.electron;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.dto.ResourceDTO;
import com.boot.ksis.service.upload.OriginalResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileUploadController {

    private final OriginalResourceService originalResourceService;

    private final String UPLOAD_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\uploads\\";

    @PostMapping("/upload-chunk")
    public ResponseEntity<String> uploadChunk(@RequestParam("chunk") MultipartFile chunk,
                                              @RequestParam("index") int index,
                                              @RequestParam("totalChunks") int totalChunks,
                                              @RequestParam("filename") String filename) {
        try{
            // 메인 파일의 경로 설정
            File mainFile = new File(UPLOAD_DIR + filename);

            // 청크를 메인 파일에 추가 (append)
            try(FileOutputStream fos = new FileOutputStream(mainFile, true)){
                fos.write(chunk.getBytes());
            }

            // 업로드 상태를 UPLOADING으로 업데이트
            originalResourceService.updateFileStatus(filename, ResourceStatus.UPLOADING);

            // 모든 청크가 업로드되었는지 확인
            boolean allChunksUploaded = (index + 1) == totalChunks;
            if (allChunksUploaded) {
                // 모든 청크가 업로드되면 상태를 COMPLETED로 설정
                originalResourceService.updateFileStatus(filename, ResourceStatus.COMPLETED);
            } else {
                // 청크가 추가로 필요하면 상태를 UPLOADING으로 설정
                originalResourceService.updateFileStatus(filename, ResourceStatus.UPLOADING);
            }

            return ResponseEntity.ok("Chunk " + index + " uploaded and appended successful.");
        }catch(IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing chunk " + index);
        }
    }

    @PostMapping("/upload-metadata")
    public ResponseEntity<String> uploadFileMetadata(@RequestBody ResourceDTO dto) {
        try {
            // 파일 메타데이터를 저장하고 상태를 UPLOADING으로 설정
            originalResourceService.saveFileMetadata(dto);
            return ResponseEntity.ok("File metadata saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving file metadata.");
        }
    }

    @PostMapping("/update-file")
    public ResponseEntity<String> updateFileStatus(@RequestParam("title") String title,
                                                   @RequestParam("status") String status) {
        try {
            // 파일 상태를 업데이트
            ResourceStatus resourceStatus = ResourceStatus.valueOf(status.toUpperCase());
            originalResourceService.updateFileStatus(title, resourceStatus);
            return ResponseEntity.ok("File status updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status provided.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating file status.");
        }
    }
}
