package com.boot.ksis.controller;

import com.boot.ksis.dto.AccountActiveDTO;
import com.boot.ksis.dto.AccountDTO;
import com.boot.ksis.dto.LoginDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping("/account")
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDTO) {
        try {
            System.out.println("Received AccountDTO: " + accountDTO);
            Account account = accountService.createAccount(accountDTO);
            return ResponseEntity.ok("Account created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating account");
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountId) throws Exception {
        System.out.println("accountId : " + accountId);
        AccountDTO account = accountService.getAccountById(accountId);
        if (account != null) {
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/account/{accountId}")
    public ResponseEntity<String> updateAccount(@PathVariable String accountId, @RequestBody AccountDTO accountDTO) throws Exception {
        try {
            boolean isUpdated = accountService.updateAccount(accountId, accountDTO);
            System.out.println(accountId + accountDTO);
            System.out.println(isUpdated);
            if (isUpdated) {
                return ResponseEntity.ok("계정 정보가 성공적으로 업데이트되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("계정 업데이트에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @PutMapping("/account/{accountId}/active")
    public ResponseEntity<?> toggleAccountActiveStatus(
            @PathVariable String accountId,
            @RequestBody AccountActiveDTO accountActiveDTO) {
        System.out.println("Account ID: " + accountId);
        System.out.println("Received isActive: " + accountActiveDTO);

        try {
            boolean isActive = accountActiveDTO.getIsActive();
            boolean updateResult = accountService.toggleActiveStatus(accountId, isActive);
            System.out.println(updateResult);
            if (updateResult) {
                return ResponseEntity.ok("Account status updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update account status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating account status");
        }
    }

//    @PutMapping("/account/{accountId}/active")
//    public ResponseEntity<?> toggleAccountActiveStatus(
//            @PathVariable String accountId,
//            @RequestBody Map<String, Object> requestBody) {
//
//        // JSON에서 isActive 값 추출
//        Boolean isActive = (Boolean) requestBody.get("isActive");
//        boolean newIsActive = isActive != null ? isActive : false;
//
//        // 로그 출력
//        logger.info("Received accountId: {}", accountId);
//        logger.info("Received request body: {}", requestBody);
//        logger.info("Received isActive: {}", isActive);
//        logger.info("Updated isActive: {}", newIsActive);
//
//        // 서비스 메소드 호출
//        accountService.toggleActiveStatus(accountId, newIsActive);
//
//        return ResponseEntity.ok("Account status updated successfully");
//    }


    @GetMapping("/accountList")
    public ResponseEntity<?> accountList(){
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        System.out.println("Received login request: " + loginDTO.getAccountId() + ", " + loginDTO.getPassword());

        boolean isValid = accountService.validateCredentials(loginDTO.getAccountId(), loginDTO.getPassword());
        try{
            return ResponseEntity.ok("{\"success\":true}");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"success\":false, \"message\":\"Invalid credentials\"}");
        }
    }


}
