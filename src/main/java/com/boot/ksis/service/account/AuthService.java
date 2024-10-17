package com.boot.ksis.service.account;

import com.boot.ksis.dto.login.JwtTokenDTO;
import com.boot.ksis.entity.RefreshToken;
import com.boot.ksis.repository.account.RefreshTokenRepository;
import com.boot.ksis.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
        Authentication authentication = authenticate(accountId, password);      // 요청 정보 검증
        JwtTokenDTO jwtToken = jwtTokenProvider.generateToken(authentication);  // 인증 정보기반 JWT토큰 생성
        saveRefreshToken(accountId, jwtToken.getRefreshToken());                // 리프레시 토큰 저장
        return jwtToken;
    }

    private Authentication authenticate(String accountId, String password) {
        // 로그인 입력값으로 authentication 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(accountId, password);
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
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
        String accessToken = extractAccessToken(authorizationHeader);           // 액세스 토큰 추출
        String accountId = jwtTokenProvider.getAccountIdFromToken(accessToken); // 액세스 토큰에서 계정정보 추출
        String refreshToken = getRefreshTokenByAccountId(accountId);            // 계정아이디에 해당하는 리프레시 토큰 조회

        if (refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {    // 리프레시 토큰 검증
            return null;
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(accountId);        // 액세스 토큰 갱신
        return JwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .build();
    }

    public boolean checkAccessToken(HttpServletRequest request) {
        String token = resolveToken(request);
        String accountId = jwtTokenProvider.getAccountIdFromToken(token);
        return !refreshTokenRepository.existsByAccountId(accountId);
    }

    private String resolveToken(HttpServletRequest request) {                       // 요청에서 액세스 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
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
}
