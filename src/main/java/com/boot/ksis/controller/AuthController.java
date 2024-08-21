package com.boot.ksis.controller;

import com.boot.ksis.dto.JwtTokenDTO;
import com.boot.ksis.entity.RefreshToken;
import com.boot.ksis.repository.RefreshTokenRepository;
import com.boot.ksis.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/get-token")
    public ResponseEntity<?> getRefreshToken(@RequestParam String accountId) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByAccountId(accountId);

        if (refreshTokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found");
        }

        // 기존 리프레시 토큰 가져오기
        RefreshToken existingToken = refreshTokenOptional.get();
        String currentRefreshToken = existingToken.getTokenValue();

        System.out.println("기존에 존재했던 리프레시 토큰 : " + currentRefreshToken);

        // 새로운 액세스 토큰 및 리프레시 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(accountId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(accountId);

        System.out.println("새로운 액세스 토큰 : " + newAccessToken);
        System.out.println("새로운 리프레시 토큰 : " + newRefreshToken);

        // 기존 리프레시 토큰을 새 토큰으로 업데이트
        existingToken.setTokenValue(newRefreshToken);
        refreshTokenRepository.save(existingToken);

        // JwtTokenDTO 객체 생성
        JwtTokenDTO tokenResponse = JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        return ResponseEntity.ok(tokenResponse);
//        if (refreshTokenOptional.isPresent()) {
//            String refreshToken = refreshTokenOptional.get().getTokenValue(); // 실제 리프레시 토큰 값 추출
//            return ResponseEntity.ok(refreshToken);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Refresh token not found");
//        }
    }
}