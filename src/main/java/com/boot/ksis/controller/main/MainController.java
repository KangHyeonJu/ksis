package com.boot.ksis.controller.main;

import com.boot.ksis.service.file.FileSizeService;
import com.boot.ksis.service.signage.SignageService;
import com.boot.ksis.service.visit.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/total")
public class MainController {
    private final FileSizeService fileSizeService;
    private final VisitService visitService;
    private final SignageService signageService;

    @GetMapping("/filesize")
    public ResponseEntity<?> totalFileSize(){
        return new ResponseEntity<>(fileSizeService.getTotalFileSize(), HttpStatus.OK);
    }

    @GetMapping("/filecount")
    public ResponseEntity<?> totalFileCount(){
        return new ResponseEntity<>(fileSizeService.getTotalFileCount(), HttpStatus.OK);
    }

    @GetMapping("/visit")
    public ResponseEntity<?> visitCount(){
        return new ResponseEntity<>(visitService.getVisitCount(), HttpStatus.OK);
    }

    @GetMapping("/device")
    public ResponseEntity<?> allDevice(Principal principal){
        String accountId = principal.getName();

        return new ResponseEntity<>(signageService.signageStatus(accountId), HttpStatus.OK);
    }

}
