package com.boot.ksis.controller.file;

import com.boot.ksis.service.file.FileTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileTypeController {

    final FileTypeService fileTypeService;

    @PostMapping("/filetype")
    public ResponseEntity<Map<String, Object>> validateFiles(@RequestParam("files") List<MultipartFile> files) {
        Map<String, Object> response = fileTypeService.validateFileTypes(files);
        return ResponseEntity.ok(response);
    }
}
