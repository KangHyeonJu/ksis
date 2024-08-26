package com.boot.ksis.service;

import com.boot.ksis.dto.Auth.JwtTokenDTO;
import com.boot.ksis.entity.RefreshToken;
import com.boot.ksis.repository.RefreshTokenRepository;
import com.boot.ksis.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JwtTokenDTO signIn(String accountId, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountId, password);
        System.out.println("Created Authentication Credentials: " + authenticationToken.getCredentials());
        try {
            // 2. 실제 검증. authenticate() 메서드를 통해 요청된 Member 에 대한 검증 진행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            System.out.println("Authenticated: " + authentication);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            JwtTokenDTO jwtToken = jwtTokenProvider.generateToken(authentication);

            // 4. 리프레시 토큰 저장 로직 추가
            saveRefreshToken(accountId, jwtToken.getRefreshToken());

            return jwtToken;
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }

//    public JwtTokenDTO refreshAccessToken(String accessToken) {
//        // 액세스 토큰에서 계정 ID 추출
//        String accountId = jwtTokenProvider.getAccountIdFromToken(accessToken);
//
//        // 새 액세스 토큰 생성
//        String newAccessToken = jwtTokenProvider.generateAccessToken(accountId);
//
//        // JwtTokenDTO 객체 생성 및 반환 (여기서는 새로 생성된 액세스 토큰만 반환)
//        return JwtTokenDTO.builder()
//                .grantType("Bearer")
//                .accessToken(newAccessToken)
//                .build();
//    }

//    @Transactional
//    public JwtTokenDTO callback(String accountId){
//        UserDetails userDetails = userDetailsService.loadUserByUsername(accountId);
//        System.out.println(userDetails);
//        try {
//                return null;
//        } catch (Exception e){
//            e.printStackTrace();
//            throw e;
//        }
//    }

    @Transactional
    public void saveRefreshToken(String accountId, String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByAccountId(accountId)
                .orElse(new RefreshToken());

        refreshToken.setAccountId(accountId);
        refreshToken.setTokenValue(refreshTokenValue);

        refreshTokenRepository.save(refreshToken);
    }

}
