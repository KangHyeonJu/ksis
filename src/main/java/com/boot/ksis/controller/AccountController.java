package com.boot.ksis.controller;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.account.AccountActiveDTO;
import com.boot.ksis.dto.account.AccountDTO;
import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.dto.login.LoginDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.service.AccountService;
import com.boot.ksis.service.AuthService;
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

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @PostMapping("/account")
    @CustomAnnotation(activityDetail = "계정 등록")
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDTO) {
        try {
            System.out.println("Received AccountDTO: " + accountDTO);
            Account account = accountService.createAccount(accountDTO);
            return ResponseEntity.ok("Account created successfully!");
        } catch (IllegalArgumentException e) {
            // 아이디 중복 예외 처리
            return ResponseEntity.status(400).body(e.getMessage());
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

    @CustomAnnotation(activityDetail = "계정 수정")
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

    @CustomAnnotation(activityDetail = "계정 상태 변경")
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

    @GetMapping("/accountList")
    public ResponseEntity<?> accountList() {
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("Received login request: " + loginDTO.getAccountId() + ", " + loginDTO.getPassword());

        try {
            boolean isValid = accountService.validateCredentials(loginDTO.getAccountId(), loginDTO.getPassword());

            if (isValid) {
                JwtTokenDTO jwtToken = authService.signIn(loginDTO.getAccountId(), loginDTO.getPassword());
                System.out.println("Created JwtToken : " + jwtToken);

                return ResponseEntity.ok(jwtToken);
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\":false, \"message\":\"아이디 및 비밀번호 확인바람\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"success\":false, \"message\":\"Invalid credentials\"}");
        }
    }
}
