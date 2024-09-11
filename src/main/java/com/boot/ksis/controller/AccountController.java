package com.boot.ksis.controller;

import com.boot.ksis.aop.CustomAnnotation;
import com.boot.ksis.dto.account.AccountActiveDTO;
import com.boot.ksis.dto.account.AccountDTO;
import com.boot.ksis.dto.login.LoginDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.service.AccountService;
import com.boot.ksis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final AuthService authService;

    @PostMapping("/admin/account")
    @CustomAnnotation(activityDetail = "계정 등록")
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.createAccount(accountDTO);
            return ResponseEntity.ok("Account created successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // 아이디 중복 예외 처리
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating account");
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountId) throws Exception {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }

    @CustomAnnotation(activityDetail = "계정 수정")
    @PutMapping("/account/{accountId}")
    public ResponseEntity<String> updateAccount(@PathVariable String accountId, @RequestBody AccountDTO accountDTO) {
        try {
            boolean isUpdated = accountService.updateAccount(accountId, accountDTO);
            return isUpdated ? ResponseEntity.ok("계정 정보가 성공적으로 업데이트되었습니다.")
                    : ResponseEntity.badRequest().body("계정 업데이트에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @CustomAnnotation(activityDetail = "계정 상태 변경")
    @PutMapping("/account/{accountId}/active")
    public ResponseEntity<?> toggleAccountActiveStatus(
            @PathVariable String accountId,
            @RequestBody AccountActiveDTO accountActiveDTO) {
        try {
            boolean updateResult = accountService.toggleActiveStatus(accountId, accountActiveDTO.getIsActive());
            return updateResult ? ResponseEntity.ok("계정정보가 수정되었습니다.")
                    : ResponseEntity.badRequest().body("계정정보 수정을 실패하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating account status");
        }
    }

    @GetMapping("/admin/accountList")
    public ResponseEntity<?> accountList() {
        return new ResponseEntity<>(accountService.getAccountList(), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            return accountService.validateCredentials(loginDTO.getAccountId(), loginDTO.getPassword())
                    ? ResponseEntity.ok(authService.signIn(loginDTO.getAccountId(), loginDTO.getPassword()))
                    : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}

