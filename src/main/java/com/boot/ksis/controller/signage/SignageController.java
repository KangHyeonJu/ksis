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
@RequestMapping("/signage")
public class SignageController {
    private final SignageService signageService;
    private final AccountListService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    @GetMapping()
    public ResponseEntity<?> pcList(){
        return new ResponseEntity<>(signageService.getSignageList(), HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<?> pcAdd(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> pcAddPost(@RequestPart("signageFormDto")SignageFormDTO signageFormDTO, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException {
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<List<String>>() {});

        signageFormDTO.setIsShow(false);
        signageService.saveNewSignage(signageFormDTO, accountList);

        return ResponseEntity.ok("재생장치가 정상적으로 등록되었습니다.");
    }

    @GetMapping("/{signageId}")
    public ResponseEntity<?> signageDtl(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/update/{signageId}")
    public ResponseEntity<?> signageDtlUpdate(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageDtl(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @PutMapping("/update/{signageId}")
    public ResponseEntity<String> signagePut(@PathVariable Long signageId, @RequestBody SignageNoticeStatusDTO signageNoticeStatusDTO) {
        System.out.println("signageNoticeStatusDTO: " + signageNoticeStatusDTO.isShowNotice());

        signageService.updateSignageStatus(signageId, signageNoticeStatusDTO.isShowNotice());
        return ResponseEntity.ok("재생장치 공지표시 상태가 정상적으로 수정되었습니다.");
    }

    @PatchMapping("/update")
    public ResponseEntity<String> signageUpdate(@RequestPart("signageFormDto") SignageFormDTO signageFormDto, @RequestPart(value="accountList",required = false) String accountListJson) throws JsonProcessingException{
        List<String> accountList = objectMapper.readValue(accountListJson, new TypeReference<>() {});

        signageService.updateSignage(signageFormDto, accountList);
        return ResponseEntity.ok("재생장치가 정상적으로 수정되었습니다.");
    }

    @GetMapping("/notice/{signageId}")
    public ResponseEntity<?> signageNotice(@PathVariable("signageId") Long signageId){
        try {
            return new ResponseEntity<>(signageService.getSignageNotice(signageId), HttpStatus.OK);
        } catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }

    @GetMapping("/resource/{signageId}")
    public ResponseEntity<?> signageResource(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getResourceList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }

    }

    @DeleteMapping("/resource/{signageId}/{encodedResourceId}")
    public ResponseEntity<?> deleteEncodedResource(@PathVariable("signageId") Long signageId, @PathVariable("encodedResourceId") Long encodedResourceId){
        try {
            signageService.deleteEncodedResource(signageId, encodedResourceId);

            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting");
        }
    }

    @GetMapping("/playlist/{signageId}")
    public ResponseEntity<?> signagePlaylistList(@PathVariable("signageId") Long signageId){
        try{
            return new ResponseEntity<>(signageService.getPlaylistList(signageId), HttpStatus.OK);
        }catch(EntityNotFoundException e){
            return new ResponseEntity<>("존재하지 않는 재생장치입니다.", HttpStatus.OK);
        }
    }
}
