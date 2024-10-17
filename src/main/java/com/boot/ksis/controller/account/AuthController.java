package com.boot.ksis.controller.account;

import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.service.sse.SseEmitterService;
import jakarta.servlet.http.HttpServletRequest;
import com.boot.ksis.service.account.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SseEmitterService sseEmitterService;

    @PostMapping("/get-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            JwtTokenDTO tokenDTO = authService.refreshAccessToken(authorizationHeader);
            return ResponseEntity.ok(tokenDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/logout/{accountId}")
    public ResponseEntity<?> logout(@PathVariable String accountId){
        authService.deleteRefreshToken(accountId);

        System.out.println("RefreshToken deleted by accountId : " + accountId);
        return ResponseEntity.ok("로그아웃 성공");
    }

    @PostMapping("/check-access-token")
    public ResponseEntity<?> checkAccessToken(HttpServletRequest request) {
        boolean isLoggedOut = authService.checkAccessToken(request);

        if (isLoggedOut) {
            return ResponseEntity.ok(Collections.singletonMap("logout", true));  // 로그아웃 상태 반환
        }
        return ResponseEntity.ok(Collections.singletonMap("logout", false));  // 로그인 유지 상태 반환
    }
}


