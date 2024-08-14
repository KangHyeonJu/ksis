package com.boot.ksis.controller.signage;

import com.boot.ksis.dto.SignageFormDTO;
import com.boot.ksis.dto.SignageNoticeStatusDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.signage.SignageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        signageFormDTO.setIsShow(false);
        signageService.saveNewSignage(signageFormDTO, accountList);

        return ResponseEntity.ok("재생장치가 정상적으로 등록되었습니다.");
    }

    @GetMapping("/signage/{signageId}")
    public ResponseEntity<?> signageDtl(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/signage/update/{signageId}")
    public ResponseEntity<?> signageDtlUpdate(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @PutMapping("/signage/update/{signageId}")
    public ResponseEntity<String> signagePut(@PathVariable Long signageId, @RequestBody SignageNoticeStatusDTO signageNoticeStatusDTO) {
        System.out.println("signageNoticeStatusDTO: " + signageNoticeStatusDTO.isShowNotice());

        signageService.updateSignageStatus(signageId, signageNoticeStatusDTO.isShowNotice());
        return ResponseEntity.ok("재생장치 공지표시 상태가 정상적으로 수정되었습니다.");
    }

    @PatchMapping("/signage/update")
    public ResponseEntity<String> signageUpdate(@RequestPart("signageFormDto") SignageFormDTO signageFormDto, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException{
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<>() {});

        signageService.updateSignage(signageFormDto, accountList);
        return ResponseEntity.ok("재생장치가 정상적으로 수정되었습니다.");
    }
}
