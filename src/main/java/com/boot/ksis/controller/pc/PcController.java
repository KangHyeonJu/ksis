package com.boot.ksis.controller.pc;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.pc.PcFormDTO;
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

    @GetMapping()
    public ResponseEntity<?> pcList(Principal principal){
        String accountId = principal.getName();

        return new ResponseEntity<>(pcService.getPcList(accountId), HttpStatus.OK);
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
        pcService.updatePc(pcFormDto, accountList);
        return ResponseEntity.ok("pc가 정상적으로 수정되었습니다.");
    }
}
