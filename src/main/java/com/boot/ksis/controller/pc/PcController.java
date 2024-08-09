package com.boot.ksis.controller.pc;

import com.boot.ksis.dto.PcFormDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.pc.PcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PcController {
    private final PcService pcService;
    private final AccountListService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/pcList")
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(pcService.getPcList(), HttpStatus.OK);
    }

    @GetMapping("/pc/new")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/pc/new")
    public ResponseEntity<String> pcAddPost(@RequestPart("pcFormDto") PcFormDTO pcFormDto, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException {
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<List<String>>() {});

        pcService.saveNewPc(pcFormDto, accountList);

        System.out.println("account?? : " + accountList);
        return ResponseEntity.ok("pc가 정상적으로 등록되었습니다.");
    }
}
