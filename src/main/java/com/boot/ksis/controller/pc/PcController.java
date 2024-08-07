package com.boot.ksis.controller.pc;

import com.boot.ksis.service.pc.PcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PcController {
    private final PcService pcService;

    @GetMapping("/pcList")
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(pcService.getPcList(), HttpStatus.OK);
    }
}
