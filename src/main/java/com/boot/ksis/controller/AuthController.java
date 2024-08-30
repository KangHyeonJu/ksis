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

    //    @PostMapping("/get-token")
//    public ResponseEntity<?> getAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
//        try {
//            // 액세스 토큰 추출
//            String accessToken = authorizationHeader.substring(7).trim();
//            System.out.println("Received Authorization: " + authorizationHeader);
//            System.out.println("Received accessToken: " + accessToken);
//
//            // 토큰에서 계정정보 추출 및 계정아이디에 해당하는 리프레시 토큰
//            String accountId = jwtTokenProvider.getAccountIdFromToken(accessToken);
//            String refreshToken = refreshTokenRepository.findByAccountId(accountId)
//                    .map(RefreshToken::getTokenValue)
//                    .orElse(null);
//
//            System.out.println("Received accountId " + accountId);
//            System.out.println("Received refreshToken " + refreshToken);
//
//            // 리프레시 토큰 검증
//            if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
//                // 리프레시 토큰이 유효하지 않으면 재로그인 유도
//                return ResponseEntity.ok(!jwtTokenProvider.validateToken(refreshToken));
//            }
//
//            // 액세스 토큰 갱신
//            String newAccessToken = jwtTokenProvider.generateAccessToken(accountId);
//            System.out.println("Generated new Access Token: " + newAccessToken);
//
//            JwtTokenDTO tokenDTO = JwtTokenDTO.builder()
//                    .grantType("Bearer")
//                    .accessToken(newAccessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//
//            return ResponseEntity.ok(tokenDTO);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }
}


