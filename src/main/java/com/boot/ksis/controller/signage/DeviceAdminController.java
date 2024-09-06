package com.boot.ksis.controller.signage;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.pc.PcFormDTO;
import com.boot.ksis.dto.signage.SignageFormDTO;
import com.boot.ksis.service.account.AccountListService;
import com.boot.ksis.service.pc.PcService;
import com.boot.ksis.service.signage.SignageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class DeviceAdminController {
    private final SignageService signageService;
    private final AccountListService accountService;
    private final PcService pcService;

    @CustomAnnotation(activityDetail = "재생장치 삭제")
    @DeleteMapping("/signage/delete")
    public ResponseEntity<?> deleteSignage(@RequestParam List<Long> signageIds){
        try{
            signageService.deleteSignage(signageIds);
            return ResponseEntity.ok("재생장치 삭제를 성공했습니다.");
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("재생장치 삭제를 실패했습니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/signage/new")
    public ResponseEntity<?> signageAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @CustomAnnotation(activityDetail = "재생장치 추가")
    @PostMapping("/signage/new")
    public ResponseEntity<String> signageAddPost(@RequestPart("signageFormDto") SignageFormDTO signageFormDTO, @RequestPart(value="accountList") List<String> accountList){
        signageFormDTO.setIsShow(false);
        signageService.saveNewSignage(signageFormDTO, accountList);

        return ResponseEntity.ok("재생장치가 정상적으로 등록되었습니다.");
    }

    @GetMapping("/pc/new")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @CustomAnnotation(activityDetail = "일반 PC 등록")
    @PostMapping("/pc/new")
    public ResponseEntity<String> pcAddPost(@RequestPart("pcFormDto") PcFormDTO pcFormDto, @RequestPart(value="accountList") List<String> accountList) {
        pcService.saveNewPc(pcFormDto, accountList);

        return ResponseEntity.ok("pc가 정상적으로 등록되었습니다.");
    }

    @CustomAnnotation(activityDetail = "일반 PC 삭제")
    @DeleteMapping("/pc/delete")
    public ResponseEntity<?> deletePcs(@RequestParam List<Long> pcIds) {
        try {
            pcService.deletePcs(pcIds);
            return ResponseEntity.ok("PCs deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting PCs");
        }
    }
}
