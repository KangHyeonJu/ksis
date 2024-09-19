package com.boot.ksis.controller.pc;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.pc.PcFormDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.pc.PcService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pc")
public class PcController {
    private final PcService pcService;
    private final AccountListService accountService;

    //PC 등록 시 USER 목록 조회
    @GetMapping("/account")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<?> pcList(Principal principal, @RequestParam String role){
        String accountId = principal.getName();

        if(role.contains("ADMIN")){
            return new ResponseEntity<>(pcService.getPcAll(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(pcService.getPcList(accountId), HttpStatus.OK);
        }
    }

    @GetMapping("/{pcId}")
    public ResponseEntity<?> pcDtl(@PathVariable("pcId") Long pcId){
        try {
            return new ResponseEntity<>(pcService.getPcDtl(pcId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 PC입니다.", HttpStatus.OK);
        }
    }

    @CustomAnnotation(activityDetail = "일반 PC 수정")
    @PatchMapping()
    public ResponseEntity<String> pcUpdate(@RequestPart("pcFormDto") PcFormDTO pcFormDto, @RequestPart(value="accountList") List<String> accountList){
        if(pcService.checkUpdateMacAddress(pcFormDto)){
            pcService.updatePc(pcFormDto, accountList);
            return ResponseEntity.ok("pc가 정상적으로 수정되었습니다.");
        }else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("이미 등록된 MAC주소입니다.");
        }
    }
}
