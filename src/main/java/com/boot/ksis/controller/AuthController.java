package com.boot.ksis.controller;

import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/get-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            JwtTokenDTO tokenDTO = authService.refreshAccessToken(authorizationHeader);
            return ResponseEntity.ok(tokenDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/logout/{accountId}")
    public ResponseEntity<?> logout(@PathVariable String accountId){
        authService.deleteRefreshToken(accountId);
        System.out.println("RefreshToken deleted by accountId : " + accountId);
        return ResponseEntity.ok("로그아웃 성공");
    }
}


