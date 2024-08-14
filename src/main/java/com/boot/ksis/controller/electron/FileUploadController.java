package com.boot.ksis.controller.electron;

import com.boot.ksis.constant.ResourceStatus;
import com.boot.ksis.constant.ResourceType;
import com.boot.ksis.dto.OriginalResourceDTO;
import com.boot.ksis.entity.OriginalResource;
import com.boot.ksis.service.upload.OriginalResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileUploadController {

    private final OriginalResourceService originalResourceService;

    // 파일이 저장되는 경로
    private final String UPLOAD_DIR = "C:\\Users\\codepc\\git\\ksis\\src\\main\\resources\\uploads\\";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("format") String format,
            @RequestParam("resolution") String resolution,
            @RequestParam("playTime") String playTime,
            @RequestParam("status") String status,
            @RequestParam("resourceType") String resourceType
    ){
        // 요청 파라미터를 System.out.println으로 출력
        System.out.println("Received file: " + file.getOriginalFilename());
        System.out.println("Title: " + title);
        System.out.println("Format: " + format);
        System.out.println("Resolution: " + resolution);
        System.out.println("PlayTime: " + playTime);
        System.out.println("Status: " + status);
        System.out.println("ResourceType: " + resourceType);

        try{
            // 파일 경로 생성
            String filePath = UPLOAD_DIR + file.getOriginalFilename();

            // 문자열을 Enum으로 변환
            ResourceStatus resourceStatusEnum = ResourceStatus.valueOf(status.toUpperCase());
            ResourceType resourceTypeEnum = ResourceType.valueOf(resourceType.toUpperCase());

            // 파일 정보를 DTO로 변환
            OriginalResourceDTO originalResourceDTO = new OriginalResourceDTO(
                    file.getOriginalFilename(),
                    title,
                    filePath,
                    Float.parseFloat(playTime),
                    format,
                    resolution,
                    file.getSize(),
                    resourceStatusEnum,
                    resourceTypeEnum
            );

            // 서비스에 DTO 전달하여 데이터베이스에 저장
            originalResourceService.saveToDatabase(originalResourceDTO);

            return ResponseEntity.ok("File uploaded successfully");
        } catch (IllegalArgumentException e) {
            // Enum 변환 오류 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status or resource type: " + e.getMessage());
        } catch (Exception e) {
            // 일반적인 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

}
