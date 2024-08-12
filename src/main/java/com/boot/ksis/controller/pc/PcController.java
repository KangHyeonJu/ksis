package com.boot.ksis.controller.pc;

import com.boot.ksis.dto.PcFormDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.pc.PcService;
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
public class PcController {
    private final PcService pcService;
    private final AccountListService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/pc")
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

    @GetMapping("/pc/{pcId}")
    public ResponseEntity<?> pcDtl(@PathVariable("pcId") Long pcId){
        try {
            return new ResponseEntity<>(pcService.getPcDtl(pcId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 PC입니다.", HttpStatus.OK);
        }
    }

    @PatchMapping("/pc")
    public ResponseEntity<String> pcUpdate(@RequestPart("pcFormDto") PcFormDTO pcFormDto, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException{
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<>() {});

        pcService.updatePc(pcFormDto, accountList);
        return ResponseEntity.ok("pc가 정상적으로 수정되었습니다.");
    }
    
    @DeleteMapping("/pc")
    public ResponseEntity<?> deletePcs(@RequestParam List<Long> pcIds) {
        try {
            pcService.deletePcs(pcIds);
            return ResponseEntity.ok("PCs deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting PCs");
        }
    }
}
