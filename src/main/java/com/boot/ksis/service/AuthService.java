package com.boot.ksis.service;

import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.entity.RefreshToken;
import com.boot.ksis.repository.RefreshTokenRepository;
import com.boot.ksis.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
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

    @Transactional
    public void saveRefreshToken(String accountId, String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByAccountId(accountId)
                .orElse(new RefreshToken());

        refreshToken.setAccountId(accountId);
        refreshToken.setTokenValue(refreshTokenValue);

        refreshTokenRepository.save(refreshToken);
    }

    public JwtTokenDTO refreshAccessToken(String authorizationHeader) {
        // 액세스 토큰 추출
        String accessToken = extractAccessToken(authorizationHeader);
        System.out.println("Received accessToken: " + accessToken);

        // 토큰에서 계정정보 추출 및 계정아이디에 해당하는 리프레시 토큰 조회
        String accountId = jwtTokenProvider.getAccountIdFromToken(accessToken);
        String refreshToken = getRefreshTokenByAccountId(accountId);
        System.out.println("Received accountId: " + accountId);
        System.out.println("Received refreshToken: " + refreshToken);

        // 리프레시 토큰 검증
        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return null;
        }

        // 액세스 토큰 갱신
        String newAccessToken = jwtTokenProvider.generateAccessToken(accountId);
        System.out.println("Generated new Access Token: " + newAccessToken);

        return JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰 검증 및 로그아웃 상태 확인
    public boolean checkAccessToken(HttpServletRequest request) {
        String token = resolveToken(request);  // 요청에서 토큰 추출
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return true;  // 토큰이 없거나 유효하지 않음 -> 로그아웃 처리 필요
        }

        String accountId = jwtTokenProvider.getAccountIdFromToken(token);  // 토큰에서 accountId 추출
        return !refreshTokenRepository.existsByAccountId(accountId);  // 리프레시 토큰이 없으면 로그아웃 상태
    }

    // 요청에서 액세스 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 이후의 실제 토큰 반환
        }
        return null;  // 토큰이 없으면 null 반환
    }

    private String extractAccessToken(String authorizationHeader) {
        return authorizationHeader.substring(7).trim();
    }

    private String getRefreshTokenByAccountId(String accountId) {
        return refreshTokenRepository.findByAccountId(accountId)
                .map(RefreshToken::getTokenValue)
                .orElse(null);
    }

    public void deleteRefreshToken(String accountId){
        refreshTokenRepository.deleteByAccountId(accountId);
    }

    // 관리자이거나 현재 사용자의 계정인지 체크하는 메서드
    public boolean isAuthorized(User currentUser, String accountId) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // 관리자이거나 사용자가 자신의 계정에 접근하는 경우 true 반환
        return isAdmin || currentUser.getUsername().equals(accountId);
    }
}
