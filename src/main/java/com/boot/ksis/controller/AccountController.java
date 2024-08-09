package com.boot.ksis.controller;

import com.boot.ksis.dto.AccountDTO;
import com.boot.ksis.dto.LoginDTO;
import com.boot.ksis.entity.Account;
import com.boot.ksis.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    @Autowired
    private AccountService accountService;

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
    public ResponseEntity<?> detailAccount(@PathVariable String accountId){
        return null;
    }

    @PutMapping("/account/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable String accountId){
        return null;
    }

    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountId) {
        try {
            accountService.deleteAccountById(accountId);
            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete account");
        }
    }

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
