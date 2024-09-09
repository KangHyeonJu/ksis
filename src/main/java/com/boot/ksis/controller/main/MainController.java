package com.boot.ksis.controller.main;

import com.boot.ksis.service.file.FileSizeService;
import com.boot.ksis.service.visit.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final FileSizeService fileSizeService;
    private final VisitService visitService;

    @GetMapping("/total/filesize")
    public ResponseEntity<?> totalFileSize(){
        return new ResponseEntity<>(fileSizeService.getTotalFileSize(), HttpStatus.OK);
    }

    @GetMapping("/total/visit")
    public ResponseEntity<?> visitCount(){
        return new ResponseEntity<>(visitService.getVisitCount(), HttpStatus.OK);
    }

}
