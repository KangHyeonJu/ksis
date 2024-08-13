package com.boot.ksis.controller.signage;

import com.boot.ksis.dto.SignageFormDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.signage.SignageService;
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
public class SignageController {
    private final SignageService signageService;
    private final AccountListService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping("/signage")
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(signageService.getSignageList(), HttpStatus.OK);
    }

    @GetMapping("/signage/new")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/signage/new")
    public ResponseEntity<String> pcAddPost(@RequestPart("signageFormDto")SignageFormDTO signageFormDTO, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException {
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<List<String>>() {});

        signageService.saveNewSignage(signageFormDTO, accountList);

        return ResponseEntity.ok("재생장치가 정상적으로 등록되었습니다.");
    }
}
