package com.boot.ksis.controller.pc;

import com.boot.ksis.service.account.AccountService;
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
    private final AccountService accountService;

    @GetMapping("/pcList")
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(pcService.getPcList(), HttpStatus.OK);
    }

    @GetMapping("/pcAdd")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }
}
